(ns tern.jdbc
  (:require [tern.misc            :refer :all]
            [tern.db              :refer :all]
            [tern.log             :as log]
            [tern.implementations :as impl]
            [clojure.java.jdbc    :as jdbc]
            [clojure.string       :as s])
  (:import [org.postgresql.util PSQLException]
           [java.sql BatchUpdateException]))

(defn- subname
  [{:keys [host port database] :as db}]
  (str "//" host ":" port "/" database))

(defn- db-spec
  ([db]
   (assoc-result db :subname subname))
  ([db database-override]
   (db-spec (assoc db :database database-override))))

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
  (jdbc/db-do-commands
    (db-spec db)
    (jdbc/create-table-ddl
      version-table
      [:version "VARCHAR(14)" "NOT NULL"]
      [:created "TIMESTAMP" "NOT NULL DEFAULT STATEMENT_TIMESTAMP()"])))

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
      (log/error e)
      (System/exit 1))
    (catch BatchUpdateException e
      (log/error "Could not initialize tern:" (batch-update-error-message e))
      (log/error e)
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

(defn- to-sql-name
  "Convert a possibly kebab-case keyword into a snakecase string"
  [k]
  (s/replace (name k) "-" "_"))

(def ^:private supported-commands
  #{:create-table :drop-table :alter-table})

(defmulti generate-sql
  (fn [c] (some supported-commands (keys c))))

(defmethod generate-sql
  :create-table
  [{table :create-table columns :columns}]
  (log/info " - Creating table" (log/highlight (name table)))
  [(apply jdbc/create-table-ddl table columns)])

(defmethod generate-sql
  :drop-table
  [{table :drop-table}]
  (log/info " - Dropping table" (log/highlight (name table)))
  [(jdbc/drop-table-ddl table)])

(defmethod generate-sql
  :alter-table
  [{table :alter-table add-columns :add-columns drop-columns :drop-columns}]
  (log/info " - Altering table" (log/highlight (name table)))
  (let [additions
        (for [[column & specs] add-columns]
          (do (log/info "    * Adding column" (log/highlight (name column)))
            (format "ALTER TABLE %s ADD COLUMN %s %s"
                    (to-sql-name table)
                    (to-sql-name column)
                    (s/join " " specs))))
        removals
        (for [column drop-columns]
          (do (log/info "    * Dropping column" (log/highlight (name column)))
            (format "ALTER TABLE %s DROP COLUMN %s"
                    (to-sql-name table)
                    (to-sql-name column))))]
    (concat removals additions)))

(defmethod generate-sql
  :default
  [command]
  (log/error "Don't know how to process command:" (log/highlight (pr-str command)))
  (System/exit 1))

(defn- update-schema-version
  [version-table version]
  (format "INSERT INTO %s (version) VALUES (%s)" version-table version))

(defn- run-migration!
  [{:keys [db version-table]} version commands]
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

(defrecord JDBCMigrator
  [config]
  Migrator
  (init    [this] (init-db! config))
  (version [this] (or (get-version config) "0"))
  (migrate [this version commands] (run-migration! config version commands)))

(impl/register! :jdbc ->JDBCMigrator)
