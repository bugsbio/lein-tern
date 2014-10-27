(ns postgres-project.core
  (:require [tern.db :as tern]))

(tern/configure!
  {:classname "org.postgresql.Driver"
   :subprotocol "postgresql"
   :subname "//127.0.0.1:5432/mydb"
   :user "root"
   :password ""})

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
