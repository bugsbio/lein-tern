(ns tern.file-test
  (:require [tern.file       :refer :all]
            [clojure.java.io :as io]
            [expectations    :refer :all]))

(expect "pants.txt"
        (fname (io/file "pants.txt")))

(expect #"^migrations/\d+-create-pants.edn$"
        (generate-name "create-pants"))
