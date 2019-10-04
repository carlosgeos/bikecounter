;; (ns bikecounter.web
;;   (:require [compojure.api.sweet :refer :all] ;collection of a lot of stuff
;;             [compojure.route :as route]
;;             [ring.util.http-response :refer :all] ;HTTP statuses and ring responses
;;             [ring.middleware.cors :refer [wrap-cors]]
;;             [hugsql.core :as hugsql]
;;             [cheshire.core :as json]
;;             [environ.core :refer [env]]
;;             [clj-time.core :as t]
;;             [clj-time.format :as tf]
;;             [clj-time.jdbc]
;;             [bikecounter.common :refer [db]])
;;   (:import (java.util Date)
;;            (org.joda.time LocalDate DateTime)))
;; ;; Reload (:reload) in requires could solve namespace and function
;; ;; definition problems.

;; ;; import sql queries as clojure functions
;; (hugsql/def-db-fns "sql/queries.sql")
;; (hugsql/def-sqlvec-fns "sql/queries.sql")

;; (try
;;   (def data (twentyfour-hours db {:ts (t/minus (t/now) (t/hours 24))}))
;;   (catch org.postgresql.util.PSQLException e
;;     (println "Could not connect to database")))

;; (def api-routes
;;   ;; defapi -> deprecated !!
;;   (api
;;    ;; (swagger-routes) also works if using basic config
;;    {:swagger {:ui "/"
;;               :spec "/swagger.json"
;;               :ring-swagger {:ignore-missing-mappings? true}
;;               :data {:info {:title "BikeCounter-Loi"}
;;                      :tags [{:name "api"}]}}}
;;    (context "/api" []
;;      :tags ["API"]
;;      (GET "/bikes" []
;;        :query-params [start_time :- DateTime, end_time :- DateTime]
;;        (ok (get-bikes-from-to db {:start_time start_time
;;                                   :end_time end_time})))
;;      (GET "/last" [] (ok data)))))


;; (def app (wrap-cors api-routes
;;                     :access-control-allow-methods [:get])) ;combine the 2 ring handlers
