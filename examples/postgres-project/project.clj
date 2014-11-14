(defproject postgres-project "0.1.0"
  :description "Example Postgresql project using Tern"
  :url "http://github.com/bugsbio/lein-tern/examples/postgres-project"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :plugins [[lein-tern "0.1.0"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [postgresql "9.3-1102.jdbc41"]]

  :profiles {:dev {:source-paths ["dev"]}}

  :tern {:init postgres-project.migrations/configure
         :migration-dir "migrations"})
