(ns tern.file-test
  (:require [tern.file       :refer :all]
            [clojure.edn     :as edn]
            [clojure.java.io :as io]
            [expectations    :refer :all]))

(def config
  {:migration-dir "migrations"})

(expect "pants.txt"
        (fname (io/file "pants.txt")))

(expect #"^migrations/\d+-create-pants.edn$"
        (generate-name config "create-pants"))

(expect {:up [] :down []}
        (let [config   {:migration-dir "examples/postgres-project/migrations"}
              filename (new-migration config "create-cats")
              content  (edn/read-string (slurp filename))]
          (.delete (io/file filename))
          content))

(expect "foo.clj"
        (basename "tern/migrations/foo.clj"))
