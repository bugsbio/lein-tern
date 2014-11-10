(defproject mysql-project "0.1.0-SNAPSHOT"
  :description "Example mysqlql project using Tern"
  :url "http://github.com/rsslldnphy/tern"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[tern "0.1.0-SNAPSHOT"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [mysql/mysql-connector-java "5.1.6"]]

  :profiles {:dev {:source-paths ["dev"]}}

  :tern {:init mysql-project.migrations/configure-tern
         :migration-dir "migrations"})
