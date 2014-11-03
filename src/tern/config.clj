(ns tern.config
  (:require [tern.log   :as log]
            [tern.color :refer [use-color]]
            [tern.misc  :refer :all]))

(def ^:private default-config
  {:migration-dir "migrations"
   :version-table "schema_versions"
   :color true
   :db {:host        "localhost"
        :port        5432
        :database    "postgres"
        :user        "postgres"
        :password    ""
        :subprotocol "postgresql"}})

(defn init-colors
  "Disable terminal colors if the user has set `:color` to false"
  [config]
  (reset! use-color (:color config))
  config)

(defn init
  "Given a leiningen project, extracts the `tern` config, merges in defaults
  where no values are set, and attempts to infer the implementation to use."
  [leiningen-config user-config]
  (-> default-config
      (deep-merge leiningen-config)
      (deep-merge user-config)
      (init-colors)))
