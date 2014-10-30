(ns leiningen.new-migration
  (:require [leiningen.tern :as tern]))

(defn new-migration
  "Generates a new migration file using the given name."
  [project name]
  (tern/tern project "new-migration" name))
