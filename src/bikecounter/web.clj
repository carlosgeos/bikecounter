(ns bikecounter.web
  (:require [compojure.api.sweet :refer :all] ;collection of a lot of stuff
            [compojure.route :as route]
            [ring.util.http-response :refer :all] ;HTTP statuses and ring responses
            [hugsql.core :as hugsql]
            [environ.core :refer [env]]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clj-time.jdbc]))
;; Reload (:reload) in requires could solve namespace and function
;; definition problems.

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
  (def data (twentyfour-hours db {:ts (t/minus (t/now) (t/hours 24))}))
  (catch org.postgresql.util.PSQLException e
    (println "Could not connect to database")))


(def api-routes
  ;; defapi -> deprecated !!
  (api
   ;; (swagger-routes) also works if using basic config
   {:swagger {:ui "/swagger"
              :spec "/swagger.json"
              :ring-swagger {:ignore-missing-mappings? true}
              :data {:info {:title "BikeCounter-Loi"}
                     :tags [{:name "api"}]}}}
   (context "/api" []
     :tags ["API"]
     (GET "/data" [] (ok data))
     (GET "/hello/:who" [who] (ok {:answer (str "Hello, " who)})))))


(defroutes site-routes
  (route/resources "/")                 ;makes the "resources/public"
                                        ;folder available at "/"
  (GET "/" [] (file-response "/index.html" {:root "resources/public"})))


(def app (routes api-routes site-routes)) ;combine the 2 ring handlers
