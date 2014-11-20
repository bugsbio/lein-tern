(defproject mysql-project "0.1.1"
  :description "Example mysqlql project using Tern"
  :url "http://github.com/bugsbio/lein-tern/examples/mysql-project"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :plugins [[lein-tern "0.1.3"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [mysql/mysql-connector-java "5.1.6"]]

  :profiles {:dev {:source-paths ["dev"]}}

  :tern {:init mysql-project.migrations/configure
         :migration-dir "migrations"})
