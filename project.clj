(defproject bikecounter "1.0.0"
  :description "Shows the biker count stats for Rue de la Loi, Brussels"
  :url "https://bikecounter-loi.herokuapp.com"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [metosin/compojure-api "2.0.0-alpha18"] ;includes compojure and ring
                 [metosin/ring-http-response "0.9.0"]
                 [ring-cors "0.1.11"]
                 [environ "1.1.0"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [com.layerware/hugsql "0.4.8"]
                 [org.postgresql/postgresql "42.1.4"]
                 [cheshire "5.8.0"]
                 [com.sendgrid/sendgrid-java "4.1.2"]
                 [clj-http "3.7.0"]
                 [clj-time "0.14.2"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.9.7"]
            [lein-environ "1.1.0"]]
  :ring {:handler bikecounter.web/app}
  :uberjar-name "bikecounter-standalone.jar"
  :profiles {:production {:env {:production true}}})
