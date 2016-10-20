(ns tern.migrate
  (:require [tern.db         :as db]
            [tern.file       :refer :all]
            [tern.misc       :refer [last-but-one]]
            [clojure.edn     :as edn]
            [clojure.java.io :as io]
            [clojure.string  :as s]))

(defn- get-migrations
  "Returns a sequence of all migration files, sorted by name."
  [{:keys [migration-dir]}]
  (->> (io/file migration-dir)
       (file-seq)
       (filter edn?)
       (sort-by fname)))

(defn parse-version
  [migration]
  (s/replace (basename migration) #"-.*$" ""))

(defn version
  "Returns the most recent migration version."
  [config]
  (when-let [migrations (seq (get-migrations config))]
    (parse-version (last migrations))))

(defn version-to
  ""
  [from migrations-pending]
  (if (seq migrations-pending)
    (parse-version (last migrations-pending))
    from))

(defn already-run?
  "Returns an anonymous function that checks if a migration is older
  than the given version. Is inclusive of the version equal to its argument."
  [current]
  (fn [migration] (<= (compare (parse-version migration) current) 0)))

(defn pending
  "Returns migrations that need to be run."
  [config current]
  (drop-while (already-run? current) (get-migrations config)))

(defn completed
  "Returns migrations that have already been run."
  [config current]
  (take-while (already-run? current) (get-migrations config)))

(defn pending-up-to
  [config current up-to]
  (let [migrations (pending config current)]
    (if up-to
      (take-while (already-run? up-to) migrations)
      migrations)))

(defn previous-version
  "Takes a migration version as its argument and returns
  the one immediately preceding it."
  [config current]
  (if-let [previous (last-but-one (completed config current))]
    (parse-version previous)
    "0"))

(defn get-migration
  "Returns the migration for a given version."
  [config version]
  (last (completed config version)))

(defn run
  "Run the given migration."
  [impl migration]
  (let [version  (parse-version migration)
        commands (-> migration slurp edn/read-string :up)]
    (db/migrate impl version commands)))

(defn rollback
  "Roll back the given migration. Uses the same code as applying a migration,
  but simply passes the `down` commands and the version of the migration that's
  being rolled back to."
  [impl migration version]
  (let [commands (-> migration slurp edn/read-string :down)]
    (db/migrate impl version commands)))

(defn reset
  "Roll back ALL migrations."
  [{:keys [config] :as impl} version]
  (let [migrations (reverse (completed config version))]
    (doseq [migration migrations]
      (rollback impl migration
                (previous-version config (parse-version migration))))))
