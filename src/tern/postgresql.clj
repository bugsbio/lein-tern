(ns tern.postgresql
  (:require [tern.db           :refer :all]
            [tern.log          :as log]
            [clojure.java.jdbc :as jdbc]
            [clojure.string    :as s])
  (:import [org.postgresql.util PSQLException]
           [java.sql BatchUpdateException]))

(def ^{:doc "Set of supported commands. Used in `generate-sql` dispatch."
       :private true}
  supported-commands
  #{:create-table :drop-table :alter-table :create-index :drop-index :create-foreign-key :drop-foreign-key})

(defn generate-table-spec
  [{:keys [primary-key] :as command}]
  (when primary-key
    (format "PRIMARY KEY (%s)" (to-sql-list primary-key))))

(defmulti generate-sql
  (fn [c] (some supported-commands (keys c))))

(defmethod generate-sql
  :create-table
  [{table :create-table columns :columns :as command}]
  (log/info " - Creating table" (log/highlight (name table)))
  (if-let [table-spec (generate-table-spec command)]
    [(apply jdbc/create-table-ddl table (conj columns [table-spec]))]
    [(apply jdbc/create-table-ddl table columns)]))

(defmethod generate-sql
  :drop-table
  [{table :drop-table}]
  (log/info " - Dropping table" (log/highlight (name table)))
  [(jdbc/drop-table-ddl table)])

(defmethod generate-sql
  :alter-table
  [{:keys [alter-table add-columns drop-columns alter-columns]}]
  (log/info " - Altering table" (log/highlight (name alter-table)))
  (let [additions
        (for [[column & specs] add-columns]
          (do (log/info "    * Adding column" (log/highlight (name column)))
            (format "ALTER TABLE %s ADD COLUMN %s %s"
                    (to-sql-name alter-table)
                    (to-sql-name column)
                    (s/join " " specs))))
        removals
        (for [column drop-columns]
          (do (log/info "    * Dropping column" (log/highlight (name column)))
            (format "ALTER TABLE %s DROP COLUMN %s"
                    (to-sql-name alter-table)
                    (to-sql-name column))))
        alterations
        (for [[column & specs] alter-columns]
          (do (log/info "    * Altering column" (log/highlight (name column)))
            (format "ALTER TABLE %s ALTER COLUMN %s %s"
                    (to-sql-name alter-table)
                    (to-sql-name column)
                    (s/join " " specs))))]
    (concat removals additions alterations)))

(defmethod generate-sql
  :create-index
  [{index   :create-index
    table   :on
    columns :columns
    unique  :unique}]
  (log/info " - Creating" (when unique "unique") "index" (log/highlight (name index)) "on" (log/highlight (name table)))
  [(format "CREATE %s INDEX %s ON %s (%s)"
           (if unique "UNIQUE" "")
           (to-sql-name index)
           (to-sql-name table)
           (s/join ", " (map to-sql-name columns)))])

(defmethod generate-sql
  :drop-index
  [{index :drop-index}]
  (log/info " - Dropping index" (log/highlight (name index)))
  [(format "DROP INDEX %s" (to-sql-name index))])

(defmethod generate-sql
  :create-foreign-key
  [{foreign-key :create-foreign-key
    table       :on
    columns     :columns
    ref-table   :ref-table
    ref-columns :ref-columns}]
  (log/info " - Creating foreign key" (log/highlight (name foreign-key)) "on" (log/highlight (name table))
    "referencing" (log/highlight (name ref-table)))
  [(format "ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s(%s)"
           (to-sql-name table)
           (to-sql-name foreign-key)
           (s/join ", " (map to-sql-name columns))
           (to-sql-name ref-table)
           (s/join ", " (map to-sql-name ref-columns)))])

(defmethod generate-sql
  :drop-foreign-key
  [{foreign-key :drop-foreign-key
    table       :on}]
  (log/info " - Dropping foreign key" (log/highlight (name foreign-key)) "on" (log/highlight (name foreign-key)))
  [(format "ALTER TABLE %s DROP CONSTRAINT %s" (to-sql-name table) (to-sql-name foreign-key))])

(defmethod generate-sql
  :default
  [command]
  (log/error "Don't know how to process command:" (log/highlight (pr-str command)))
  (System/exit 1))

(defn- database-exists?
  [db]
  (jdbc/query
    (db-spec db "postgres")
    ["SELECT 1 FROM pg_catalog.pg_database
     WHERE datname = ?" (:database db)]
    :result-set-fn first))

(defn- table-exists?
  [db table]
  (jdbc/query
    (db-spec db)
    ["SELECT 1 FROM information_schema.tables WHERE table_name = ?" table]
    :result-set-fn first))

(defn- create-database
  [db]
  (jdbc/db-do-commands
    (db-spec db "postgres") false
    (format "CREATE DATABASE %s" (:database db))))

(defn- create-version-table
  [db version-table]
  (apply jdbc/db-do-commands
         (db-spec db)
         (generate-sql
           {:create-table version-table
            :columns [[:version "VARCHAR(14)" "NOT NULL"]
                      [:created "TIMESTAMP" "NOT NULL DEFAULT STATEMENT_TIMESTAMP()"]]})))

(defn- psql-error-message
  [e]
  (s/replace (.getMessage e) #"^(FATAL|ERROR): " ""))

(defn- batch-update-error-message
  [e]
  (s/replace (.getMessage (.getNextException e)) #"^(FATAL|ERROR): " ""))

(defn- init-db!
  [{:keys [db version-table]}]
  (try
    (when-not (database-exists? db)
      (create-database db)
      (log/info "Created database:" (:database db)))
    (when-not (table-exists? db version-table)
      (create-version-table db version-table)
      (log/info "Created table:   " version-table))
    (catch PSQLException e
      (log/error "Could not initialize tern:" (psql-error-message e))
      (System/exit 1))
    (catch BatchUpdateException e
      (log/error "Could not initialize tern:" (batch-update-error-message e))
      (System/exit 1))))

(defn- get-version
  [{:keys [db version-table]}]
  (try
    (jdbc/query
      (db-spec db)
      [(format "SELECT version FROM %s
               ORDER BY created DESC
               LIMIT 1" version-table)]
      :row-fn :version
      :result-set-fn first)))

(defn- update-schema-version
  [version-table version]
  (format "INSERT INTO %s (version) VALUES (%s)" version-table version))

(defn- run-migration!
  [{:keys [db version-table]} version commands]
  (when-not (vector? commands)
    (log/error "Values for `up` and `down` must be vectors of commands"))
  (try
    (apply jdbc/db-do-commands
           (db-spec db)
           (conj (into [] (mapcat generate-sql commands))
                 (update-schema-version version-table version)))
    (catch PSQLException e
      (log/error "Migration failed:" (psql-error-message e))
      (log/error e)
      (System/exit 1))
    (catch BatchUpdateException e
      (log/error "Migration failed:" (batch-update-error-message e))
      (log/error e)
      (System/exit 1))))

(defn- validate-commands
  [commands]
  (cond (and (vector? commands)
             (every? map? commands)) commands
        (map? commands) [vec commands]
        nil nil
        :else (do
                (log/error "The values for `up` and `down` must be either a map or a vector of maps.")
                (System/exit 1))))

(defrecord PostgresqlMigrator
  [config]
  Migrator
  (init[this]
    (init-db! config))
  (version [this]
    (or (get-version config) "0"))
  (migrate [this version commands]
    (run-migration! config version (validate-commands commands))))
