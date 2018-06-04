(ns bikecounter.fetch
  (:require [clojure.java.jdbc :as j]
            [environ.core :refer [env]]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [hugsql.core :as hugsql]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clj-time.jdbc])    ;allows sending DateTime objects to DB
  (:import (com.sendgrid SendGrid Email Content Mail Request Method)))

;; The path is relative to the classpath (not proj dir!), so "src" is
;; not included in the path.  The same would apply if the sql was
;; under "resources/..."  Also, notice the under_scored path compliant
;; with Clojure file paths for hyphenated namespaces
(hugsql/def-db-fns "sql/queries.sql")
(hugsql/def-sqlvec-fns "sql/queries.sql")

(def CORRECT_ID 3)                      ;API bs


(defn notify-of-failure
  "Sends an email using SendGrid's API, saying that something went wrong
  with the bike counter resource"
  []
  (def sg (SendGrid. (env :sendgrid-api-key)))
  (def email (Mail. (Email. "bikecounter@herokuapps.com") ;from
                    "[bikecounter] Failure notification"  ;subject
                    (Email. (env :email))                 ;to
                    (Content. "text/plain" (str "Cron job failed to fetch the remote GeoJSON resource on "
                                                (tf/unparse (tf/formatter :rfc822) (t/now))))))

  (def req (Request.))
  (.setMethod req Method/POST)
  (.setEndpoint req "mail/send")
  (.setBody req (.build email))
  (.api sg req))


(def db {:dbtype     (env :db-type)
         :dbname     (env :db-name)
         :host       (env :db-host)
         :user       (env :db-user)
         :password   (env :db-password)
         :ssl        true
         :sslfactory "org.postgresql.ssl.NonValidatingFactory"})


(def RESOURCE_URL "http://data-mobility.brussels/geoserver/bm_bike/wfs?service=wfs&version=1.1.0&request=GetFeature&typeName=bm_bike:rt_counting&outputFormat=json")


(def raw-data
  (try
    (as-> (json/parse-string (:body (client/get RESOURCE_URL))) obj
      (get obj "features")
      (filter #(= (get (get % "properties") "id") CORRECT_ID) obj)
      (first obj)
      (get obj "properties"))
    (catch Exception e
      (notify-of-failure))))


;; If the above fails, then everything fails since we can't continue
;; with a SendGrid Response as raw-data.


(def bikers-currently {:ts      (tf/parse (tf/formatter :date-time-no-ms)
                                          (raw-data "cnt_time"))
                       :today   (raw-data "day_cnt")
                       :parcial (raw-data "hour_cnt")})


(defn insert-record-policy
  "Very simple, if record does not exist already, insert it, otherwise
  do nothing. Times are in UTC (timezone +0). Brussels is CEST (+2) in
  summer and CET (+1) in november etc."
  []
  (let [last-record  (select-last-record db)
        last-time    (:ts last-record)
        current-time (bikers-currently :ts)]
    ;; Insert normal records...
    (if (or (nil? last-time)
            (not= last-time current-time))
      (add-record db bikers-currently)
      "Nothing to do")
    ;; Amend hourly. At midnight the counter is reset to 0 so those
    ;; bikers are lost, we can only update the column with the last value
    ;; seen (first clause). Also, there is a small correction to account
    ;; for bikers who pass in the last 5 mins of the hour - second
    ;; clause.
    (when (some? last-time)              ;database is not empty
      (def realvalue (cond
                       ;; First clause. Order is important
                       (and (= (.getHourOfDay last-time) 22)
                            (= (.getHourOfDay current-time) 23))
                       (:parcial last-record)
                       ;; Second clause
                       (= (inc (.getHourOfDay last-time)) (.getHourOfDay current-time)) ;its the next hour
                       (-> (:today bikers-currently)
                           (- ,,, (:today last-record) (:parcial bikers-currently))
                           (+ ,,, (:parcial last-record)))))

      (amend-hourly db {:id       (:id last-record)
                        :thishour realvalue}))))



(defn -main
  [& arg]
  (insert-record-policy))
