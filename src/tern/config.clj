(ns tern.config
  "A namespace to store the config values used
  by `tern`. There's no real need for them to be
  atoms, except my laziness in not wanting to pass
  them around."
  (:require [tern.interrobang :refer [reset!?]]))

(def ^{:doc "The directory containing migrations."}
  migration-dir
  (atom "migrations"))

(def ^{:doc "The DB connection details used by `tern`."}
  db
  (atom {:classname "org.postgresql.Driver"
         :subprotocol "postgresql"
         :subname "//127.0.0.1:5432/mydb"
         :user "root"
         :password ""}))

(def ^{:doc "The migrator implementation to use."}
  implementation
  (atom :postgresql))

(defn init!
  "Initialize configuration from project."
  [project]
  (let [config (:tern project {})]
    (reset!? db             (:db config))
    (reset!? implementation (:implementation config))
    (reset!? migration-dir  (:migration-dir config))))
