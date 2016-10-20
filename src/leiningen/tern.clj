(ns leiningen.tern
  (:require [tern.config            :as config]
            [tern.commands          :as c]
            [tern.log               :as log]
            [tern.version           :refer [tern-version]]
            [leiningen.core.project :as project]
            [leiningen.core.eval    :refer [eval-in-project]]))

(defn
  ^{:subtasks
    [#'c/init
     #'c/config
     #'c/version
     #'c/migrate
     #'c/pending
     #'c/rollback
     #'c/reset
     #'c/new-migration]}
  tern
  "Create, run, and roll back database migrations.
  For the lazy among you, the commands `migrate` and `new-migration` can be
  called without using the `tern` prefix."
  ([project]
   (log/info "The" (log/highlight "tern") "task requires a subcommand.")
   (log/info "Run" (log/highlight "lein help tern") "for a list of available commands."))
  ([project cmd & args]
   (let [user-config-fn (or (-> project :tern :init) 'tern.user/config)
         tern-profile   {:dependencies [['lein-tern tern-version]]}]
     (eval-in-project
       (project/merge-profiles project [tern-profile])
       `(do
          (require '[tern.commands :as c])
          (require '[tern.config :as config])
          (require '~(symbol (namespace user-config-fn)))
          (let [config# (config/init ~(:tern project) (~user-config-fn))]
            (case ~cmd
              "init"          (c/init          config#)
              "config"        (c/config        config#)
              "version"       (c/version       config#)
              "migrate"       (c/migrate       config# ~(first args))
              "pending"       (c/pending       config# ~(first args))
              "rollback"      (c/rollback      config#)
              "reset"         (c/reset         config#)
              "new-migration" (c/new-migration config# ~(first args))))
          (System/exit 0))))))
