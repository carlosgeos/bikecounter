(defproject bikecounter "1.0.0"
  :description "Shows the biker count stats for Rue de la Loi, Brussels"
  :url "https://bikecounter-loi.herokuapp.com"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.6.0"]
                 [ring/ring-jetty-adapter "1.6.3"]
                 [environ "1.1.0"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.3.1"]
            [lein-ring "0.9.7"]]
  :ring {:handler bikecounter.web/app}
  :hooks [environ.leiningen.hooks]
  :uberjar-name "bikecounter-standalone.jar"
  :profiles {:production {:env {:production true}}})
