(ns bikecounter.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello World"})


(defn- str-to [num]
  (apply str (interpose ", " (range 1 (inc num)))))

(defn- str-from [num]
  (apply str (interpose ", " (reverse (range 1 (inc num))))))

(defroutes app
  (GET "/" []
       (splash))
  (GET "/count-up/:to" [to]
       (str-to (Integer. to)))
  (GET "/count-down/:from" [from]
       (str-from (Integer. from)))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))
