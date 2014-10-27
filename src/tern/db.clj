(ns tern.db
  (:require [tern.config :as config]))

(defn configure!
  "Set the DB connection details `tern` should use."
  [db-spec]
  (reset! config/db db-spec))

(defprotocol Migrator
  (init    [this])
  (version [this]))
