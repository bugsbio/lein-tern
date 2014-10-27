(defproject tern "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.8.0"]]
  :profiles {:dev {:dependencies [[expectations "2.0.9"]]
                   :plugins [[lein-autoexpect "1.0"]
                             [lein-expectations "0.0.8"]]}}
  :eval-in-leiningen true)
