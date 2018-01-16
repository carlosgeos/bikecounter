(ns bikecounter.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [ring.middleware.defaults :refer :all]
            [hugsql.core :as hugsql]
            [environ.core :refer [env]]
            [selmer.parser :refer :all]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clj-time.jdbc]))
;; Reload (:reload) in requires could solve namespace and function definition problems.

(selmer.parser/cache-off!) ;; Otherwise, layout templates are not reloaded, only the extending one

;; import sql queries as clojure functions
(hugsql/def-db-fns "sql/queries.sql")
(hugsql/def-sqlvec-fns "sql/queries.sql")


(def db {:dbtype     (env :db-type)
         :dbname     (env :db-name)
         :host       (env :db-host)
         :user       (env :db-user)
         :password   (env :db-password)
         :ssl        true
         :sslfactory "org.postgresql.ssl.NonValidatingFactory"})

(try
  (def data (map #(assoc % :ts (t/hour (t/to-time-zone (:ts %) (t/time-zone-for-id "Europe/Brussels"))))
                 (twentyfour-hours db {:ts (t/minus (t/now) (t/hours 24))})))
  (catch org.postgresql.util.PSQLException e
    (println "Could not connect to database")))


(defroutes app
  (GET "/" [] (render-file "templates/home.html" {:data data})))


(def site
  (wrap-defaults app (assoc site-defaults :static {:resources ["public", "META-INF/resources"]})))
