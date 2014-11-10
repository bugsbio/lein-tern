(ns mysql-project.migrations)

(defn configure-tern []
  {:db {:classname   "com.mysql.jdbc.Driver"
        :subprotocol "mysql"
        :database    "mysql_example_db"
        :user        "root"
        :port        "3306"
        :password    ""}})
