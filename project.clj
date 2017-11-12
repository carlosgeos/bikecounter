(defproject bikecounter "1.0.0"
  :description "Shows the biker count stats for Rue de la Loi, Brussels"
  :url "https://bikecounter-loi.herokuapp.com"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.6.0"]
                 [ring "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [org.clojure/java.jdbc "0.7.3"]
                 [com.layerware/hugsql "0.4.8"]
                 [org.postgresql/postgresql "42.1.4"]
                 [environ "1.1.0"]
                 [selmer "1.11.3"]
                 [deraen/sass4clj "0.3.1"]
                 [org.webjars/bootstrap-sass "3.3.7"]]
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.9.7"]
            [lein-environ "1.1.0"]
            [deraen/lein-sass4clj "0.3.1"]]
  :sass {:target-path "resources/public/css/"
         :source-paths ["resources/sass/"]
         :source-map true
         :output-style :compressed}
  :ring {:handler bikecounter.web/site}
  :uberjar-name "bikecounter-standalone.jar"
  :profiles {:production {:env {:production true}}})
