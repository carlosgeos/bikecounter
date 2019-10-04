(defproject bikecounter "1.0.0"
  :description "Shows the number of cyclists through time at different points in Brussels"
  :url "https://bikecounter-loi.herokuapp.com"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/java.jdbc "0.7.10"]
                 [environ "1.1.0"]
                 [com.layerware/hugsql "0.5.1"]
                 [org.postgresql/postgresql "42.2.8.jre7"]
                 [cheshire "5.9.0"]
                 [com.sendgrid/sendgrid-java "4.4.1"]
                 [com.sendgrid/java-http-client "4.2.0"]
                 [clj-http "3.10.0"]
                 [clojure.java-time "0.3.2"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-environ "1.1.0"]]
  :repl-options {:init-ns bikecounter.core}
  :aot :all)
