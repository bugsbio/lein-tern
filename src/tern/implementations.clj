(ns tern.implementations)

(def ^{:doc "A map of available migrator implementations."
       :private true}
  constructor-store
  (atom {}))

(defn register!
  "Register a new tern implementation. This function
  takes a keyword to identify the implementation and
  a constructor function, that, given the required
  db-spec & configuration, will construct an object
  that implements the `Migrator` protocol."
  [k constructor]
  (swap! constructor-store assoc k constructor))

(defn factory
  "Factory create a `Migrator` for the given DB
  implementation and config."
  [{:keys [impl] :as config}]
  (when-let [new-impl (@constructor-store impl)]
    (new-impl config)))
