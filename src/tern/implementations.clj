(ns tern.implementations
  (:require [tern.postgresql :as postgresql]
            [tern.mysql      :as mysql]
            [tern.log        :as log]))

(def ^{:doc "A map of available migrator implementations."
       :private true}
  constructor-store
  (atom {:postgresql postgresql/->PostgresqlMigrator :mysql mysql/->MysqlMigrator}))

(defn register!
  "Register a new tern implementation. This function
  takes a keyword to identify the implementation and
  a constructor function, that, given the required
  db-spec & configuration, will construct an object
  that implements the `Migrator` protocol."
  [k constructor]
  (swap! constructor-store assoc k constructor))

(defn factory
  "Factory create a `Migrator` for the given DB
  implementation and config."
  [{{:keys [subprotocol]} :db :as config}]
  (if-let [new-impl (@constructor-store (keyword subprotocol))]
    (new-impl config)
    (do
      (log/error "Sorry, support for" subprotocol "is not implemented yet.")
      (System/exit 1))))
