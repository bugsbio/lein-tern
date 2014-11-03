(ns tern.implementations-test
  (:require [tern.implementations :refer :all]
            [tern.db              :refer :all]
            [expectations         :refer :all]))

(defrecord DummyMigrator [config]
  Migrator
  (init    [this] :init)
  (version [this] :version))

(register! :dummy ->DummyMigrator)

(expect {:config {:db {:subprotocol "dummy"}}}
        (factory {:db {:subprotocol "dummy"}}))
