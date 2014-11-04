(ns tern.config-test
  (:require [tern.config  :refer :all]
            [expectations :refer :all]))

(def dummy-project
  {:db {:classname "org.postgresql.Driver"
        :subprotocol "postgresql"
        :database "mydb"
        :user "root"
        :password ""}
   :migration-dir "tern-migrations"})

(expect {:version-table "schema_versions"
         :db  {:classname "org.postgresql.Driver"
               :subprotocol "postgresql"
               :host "localhost"
               :port 5432
               :database "mydb"
               :password "secret!"
               :user "root"}
         :color true
         :migration-dir "tern-migrations"}
        (init dummy-project {:db {:password "secret!"}}))
