(ns bikecounter.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [ring.middleware.defaults :refer :all]
            [hugsql.core :as hugsql]
            [environ.core :refer [env]]
            [selmer.parser :refer :all]))
;; Reload (:reload) for requires could solve namespace and function definition problems.

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


(select-last-record db)

(defroutes app
  (GET "/" [] (render-file "templates/home.html" (select-last-record db))))
;; (GET "/count-up/:to" [to]
;;      (str-to (Integer. to)))
;; (GET "/count-down/:from" [from]
;;      (str-from (Integer. from)))
;; (ANY "*" []
;;      (layout/application (slurp (io/resource "404.html")))))


(def site
  (wrap-defaults app (assoc site-defaults :static {:resources ["public", "META-INF/resources"]})))