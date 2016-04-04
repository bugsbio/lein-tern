(defproject lein-tern "0.1.4-SNAPSHOT"
  :description "Migrations as data"
  :url "http://github.com/bugsbio/lein-tern"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :signing {:gpg-key "CF73E6ED"}
  :scm  {:name "git"
         :url "https://github.com/bugsbio/lein-tern"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.8.0"]
                 [org.clojure/java.jdbc "0.3.2"]
                 [postgresql "9.3-1102.jdbc41"]
                 [java-jdbc/dsl "0.1.0"]]
  :plugins [[s3-wagon-private "1.2.0"]]
  :repositories [["private" {:url "s3p://bugsbio/releases/"
                             :username :env/aws_access_key
                             :passphrase :env/aws_secret_key}]]
  :profiles {:dev {:dependencies [[expectations "2.0.9"]]
                   :plugins [[lein-autoexpect "1.0"]
                             [lein-expectations "0.0.8"]]}}
  :eval-in-leiningen true)
