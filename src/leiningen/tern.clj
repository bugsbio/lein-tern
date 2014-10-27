(ns leiningen.tern
  (:require [tern.config             :as config]
            [tern.commands           :as c]
            [leiningen.new-migration :refer [new-migration]]))

(defn
  ^{:subtasks [#'c/init #'c/version #'c/new-migration]}
  tern
  "Create, run, and roll back database migrations.
  For the lazy among you, the command `new-migration` can be called without
  using the `tern` prefix."
  [project cmd & args]
  (config/init! project)
  (case cmd
    "init"          (c/init)
    "new-migration" (c/new-migration (first args))
    "config"        (c/print-config)
    "version"       (c/version)))
