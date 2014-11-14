(ns postgres-project.migrations)

(defn configure []
  {:db {:classname   "org.postgresql.Driver"
        :subprotocol "postgresql"
        :database    "postgres_example_db"
        :user        (System/getenv "USER")
        :password    ""}})
