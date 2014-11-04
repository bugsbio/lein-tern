(ns tern.migrate-test
  (:require [tern.migrate :refer :all]
            [expectations :refer :all]))

(expect "20141104091724"
        (version {:migration-dir "examples/postgres-project/migrations"}))
