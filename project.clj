(defproject bikecounter "1.0.0"
  :description "Shows the number of cyclists through time at different points in Brussels"
  :url "https://bikecounter-loi.herokuapp.com"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.10.0"]
                 [clj-http "3.10.0"]
                 [clj-sendgrid "0.1.2"]
                 [clojure.java-time "0.3.2"]
                 [com.amazonaws/aws-lambda-java-core "1.2.0"]
                 [congomongo "2.2.1"]
                 [environ "1.1.0"]
                 [failjure "2.0.0"]
                 [org.clojure/clojure "1.10.1"]
                 [prismatic/schema "1.1.12"]
                 [funcool/cuerdas "2.2.1"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-environ "1.1.0"]]
  :repl-options {:init-ns bikecounter.core}
  :aot :all)
