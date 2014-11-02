(ns leiningen.migrate
  (:require [leiningen.tern :as tern]))

(defn migrate
  "Runs any pending migrations to bring the database up to the latest version."
  [project]
  (tern/tern project "migrate"))
