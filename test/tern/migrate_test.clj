(ns tern.migrate-test
  (:require [tern.migrate :refer :all]
            [expectations :refer :all]))

(expect "20141117094728"
        (version {:migration-dir "examples/postgres-project/migrations"}))
