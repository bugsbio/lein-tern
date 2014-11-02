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

(defn- infer-implementation
  "Infers the correct implementation to use for a given DB spec.
  Unfortunately this pretty badly violates the open/closed principle,
  but... we'll think about that later."
  [{db-spec :db :as config}]
  (or (:impl config)
      (case (:subprotocol db-spec)
        "postgresql" :jdbc
        (do (if-let [subprotocol (:subprotocol db-spec)]
              (log/error "Sorry," subprotocol "is not yet supported.")
              (log/error "Your DB config appears to be incomplete. Run" (log/highlight "lein tern config") "to check it."))
          (System/exit 1)))))

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
      (init-colors)
      (assoc-result :impl infer-implementation)))
