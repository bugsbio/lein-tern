(ns tern.postgresql
  (:require [tern.db :refer :all]))

(defrecord PostgresqlMigrator
  [db-spec]
  Migrator
  (init    [this] (println "Init-ing postgres"))
  (version [this] (println "Getting version from postgres")))

(defn impl
  "Create a new instance of a migrator implementation.
  A function with this name must exist for each migrator
  implementation."
  [db-spec]
  (->PostgresqlMigrator db-spec))
