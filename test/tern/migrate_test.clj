(ns tern.migrate-test
  (:require [tern.migrate :refer :all]
            [expectations :refer :all]))

(expect "20141102101737"
        (version {:migration-dir "migrations"}))
