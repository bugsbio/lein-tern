(ns tern.commands
  (:require [tern.config          :as config]
            [tern.db              :as db]
            [tern.file            :as f :refer [fname]]
            [tern.implementations :as impl]
            [tern.log             :as log]
            [tern.migrate         :as migrate]))

(defn init
  "Creates the table used by `tern` to track versions."
  [config]
  (db/init (impl/factory config)))

(defn version
  "Prints the database's current version."
  [config]
  (log/info "The database is at version" (db/version (impl/factory config))))

(defn new-migration
  "Creates a new migration file using the given name.
  It is preceded by a timestamp, so as to preserve ordering."
  [config name]
  (log/info "Creating:" (f/new-migration config name)))

(defn config
  "Prints the current configuration values used by `tern`."
  [{:keys [version-table migration-dir db color]}]
  (log/info (log/keyword ":migration-dir ") migration-dir)
  (log/info (log/keyword ":version-table ") version-table)
  (log/info (log/keyword ":color         ") color)
  (log/info (log/keyword ":db"))
  (log/info (log/keyword "  :host        ") (:host db))
  (log/info (log/keyword "  :port        ") (:port db))
  (log/info (log/keyword "  :database    ") (:database db))
  (log/info (log/keyword "  :user        ") (:user db))
  (log/info (log/keyword "  :password    ") (:password db))
  (log/info (log/keyword "  :subprotocol ") (:subprotocol db)))

(defn migrate
  "Runs any pending migrations to bring the database up to the latest version."
  [config]
  (let [impl    (impl/factory config)
        from    (db/version impl)
        to      (migrate/version config)
        pending (migrate/pending config from)]
    (log/info "#######################################################")
    (log/info "Migrating from version" (log/highlight from) "to" (log/highlight to))
    (log/info "#######################################################")
    (doseq [migration pending]
      (log/info "Processing" (log/filename (fname migration)))
      (migrate/run impl migration))
    (if (seq pending)
      (log/info "Migration complete")
      (log/info "There were no changes to apply"))))

(defn rollback
  "Rolls back the most recent migration"
  [config]
  (let [impl      (impl/factory config)
        from      (db/version impl)
        to        (migrate/previous-version config from)]
    (log/info "#######################################################")
    (log/info "Rolling back from version" (log/highlight from) "to" (log/highlight to))
    (log/info "#######################################################")
    (if-let [migration (migrate/get-migration config from)]
      (do
        (migrate/rollback impl migration to)
        (log/info "Rollback complete"))
      (log/info "There were no changes to roll back"))))

(defn reset
  "Reverts all migrations, returning database to it's original state."
  [config]
  (println "Are you sure? This will roll back ALL migrations. y/n")
  (when (= "y" (read-line))
    (let [impl    (impl/factory config)
          version (db/version impl)]
      (log/info "#######################################################")
      (log/info (log/danger "Rolling back ALL migrations"))
      (log/info "#######################################################")
      (migrate/reset impl version)
      (log/info "Reset complete"))))
