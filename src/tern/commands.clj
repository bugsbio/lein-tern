(ns tern.commands
  (:require [tern.config         :as config]
            [tern.file           :as f]
            [tern.db             :as db]
            [tern.postgresql     :as postgresql]
            [leiningen.core.main :refer [info]]))

(def
  ^{:private true
    :doc "The implementation instance to use."}
  impl
  (delay (case @config/implementation
           :postgresql (postgresql/impl @config/db))))

(defn init
  "Creates the table used by `tern` to track versions."
  []
  (db/init @impl))

(defn version
  "Prints the database's current version."
  []
  (info "Soon!"))

(defn new-migration
  "Creates a new migration file using the given name.
  It is preceded by a timestamp, so as to preserve ordering."
  [name]
  (let [filename (f/generate-name name)]
    (spit filename (pr-str {}))
    (info filename)))

(defn print-config
  "Prints the current configuration values used by `tern`."
  []
  (info "Tern is currently set to use these config values:")
  (info "migration-dir:    " @config/migration-dir))

