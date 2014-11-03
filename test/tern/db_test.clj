(ns tern.db-test
  (:require [tern.db      :refer :all]
            [expectations :refer :all]))

;; concatenates db config
(expect "//localhost:5432/animals"
        (subname {:host "localhost"
                  :port 5432
                  :database "animals"}))

;; `db-spec` assocs the subname into the db configuration
(expect {:host "localhost"
         :port 5432
         :database "animals"
         :subname "//localhost:5432/animals"}
        (db-spec {:host "localhost"
                  :port 5432
                  :database "animals"}))

;; `:database` can be overridden
(expect {:host "localhost"
         :port 5432
         :database "pets"
         :subname "//localhost:5432/pets"}
        (db-spec {:host "localhost"
                  :port 5432
                  :database "animals"} "pets"))

;; snake-cases and stringifies
(expect "favourite_foods"
        (to-sql-name :favourite-foods))
