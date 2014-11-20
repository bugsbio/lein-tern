(ns tern.file
  (:require [tern.config     :as config]
            [clojure.string  :as s]
            [clj-time.core   :as t]
            [clj-time.format :as f])
  (:import [java.io File]))

(def ^:private timestamp-formatter
  (f/formatter "yyyyMMddHHmmss"))

(def ^:private empty-migration
  (s/join "\n" ["{:up" " []" " :down" " []}"]))

(defn- timestamp!
  []
  (f/unparse timestamp-formatter (t/now)))

(defn generate-name
  "Generates a name that will order chronologically by
  prepending the current timestamp to the name supplied
  by the user."
  [migration-dir name]
  (str migration-dir File/separator (timestamp!) "-" name ".edn"))

(defn new-migration
  "Generates a new migration filename, creates it,
  and returns its name. Creates the migration directory if it
  doesn't already exist."
  [{:keys [migration-dir]} name]
  (.mkdir (File. migration-dir))
  (doto (generate-name migration-dir name) (spit empty-migration)))

(defn fname
  "Wrapper for file.getName() because I am a bad person,
  and can't just leave Clojure's Java interop alone and
  get on with more important things instead."
  [f]
  (if (string? f) f (.getName f)))

(defn basename
  "Get the basename from a file or filename."
  [f]
  (s/replace (fname f) #"^.*\/" ""))

(defn extension
  "Get the extension from a file or filename."
  [f]
  (or (second (re-find #"\.([^\.]+)$" (fname f))) ""))

(defn has-exension?
  "Checks if a file has the given extension."
  [ext f]
  (= ext (extension f)))

(def edn?
  ^{:doc "Checks if a file has the `edn` extension." }
  (partial has-exension? "edn"))
