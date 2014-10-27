(ns leiningen.new-migration
  (:require [tern.config   :as config]
            [tern.commands :as c]))

(defn new-migration
  "Generates a new migration file using the given name."
  [project name]
  (config/init! project)
  (c/new-migration name))
