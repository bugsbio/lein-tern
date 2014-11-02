(defproject postgres-project "0.1.0-SNAPSHOT"
  :description "Example Postgresql project using Tern"
  :url "http://github.com/rsslldnphy/tern"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[tern "0.1.0-SNAPSHOT"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [postgresql "9.3-1102.jdbc41"]]

  :profiles {:dev {:source-paths ["dev"]}}

  :tern {:init postgres-project.migrations/configure-tern
         :migration-dir "migrations"})
