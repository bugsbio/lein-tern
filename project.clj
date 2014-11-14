(defproject lein-tern "0.1.0-SNAPSHOT"
  :description "Migrations as data"
  :url "http://github.com/bugsbio/lein-tern"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.8.0"]
                 [org.clojure/java.jdbc "0.3.2"]
                 [postgresql "9.3-1102.jdbc41"]
                 [java-jdbc/dsl "0.1.0"]]
  :profiles {:dev {:dependencies [[expectations "2.0.9"]]
                   :plugins [[lein-autoexpect "1.0"]
                             [lein-expectations "0.0.8"]]}}
  :eval-in-leiningen true)
