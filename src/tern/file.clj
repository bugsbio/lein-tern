(ns tern.file
  (:require [tern.config     :as config]
            [clj-time.core   :as t]
            [clj-time.format :as f])
  (:import [java.io File]))

(def ^:private timestamp-formatter
  (f/formatter "yyyyMMddHHmmss"))

(defn- timestamp!
  []
  (f/unparse timestamp-formatter (t/now)))

(defn generate-name
  "Generates a name that will order chronologically by
  prepending the current timestamp to the name supplied
  by the user."
  [name]
  (str @config/migration-dir File/separator (timestamp!) "-" name ".edn"))

(defn fname
  "Wrapper for file.getName() because I am a bad person,
  and can't just leave Clojure's Java interop alone and
  get on with more important things instead."
  [f]
  (.getName f))
