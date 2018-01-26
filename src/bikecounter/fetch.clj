(ns bikecounter.fetch
  (:require [clojure.java.jdbc :as j]
            [environ.core :refer [env]]
            [clj-http.client :as client]
            [cheshire.core :as json]
            [hugsql.core :as hugsql]
            [clj-time.core :as t]
            [clj-time.format :as tf]
            [clj-time.jdbc]))           ;allows sending DateTime objects to DB


;; The path is relative to the classpath (not proj dir!), so "src" is
;; not included in the path.  The same would apply if the sql was
;; under "resources/..."  Also, notice the under_scored path compliant
;; with Clojure file paths for hyphenated namespaces
(hugsql/def-db-fns "sql/queries.sql")
(hugsql/def-sqlvec-fns "sql/queries.sql")


(def db {:dbtype     (env :db-type)
         :dbname     (env :db-name)
         :host       (env :db-host)
         :user       (env :db-user)
         :password   (env :db-password)
         :ssl        true
         :sslfactory "org.postgresql.ssl.NonValidatingFactory"})


(def RESOURCE_URL "http://data-mobility.brussels/geoserver/bm_bike/wfs?service=wfs&version=1.1.0&request=GetFeature&typeName=bm_bike:rt_counting&outputFormat=json")


(def raw-data
  (-> (json/parse-string (:body (client/get RESOURCE_URL)))
      (get "features")
      first
      (get "properties")))


(def bikers-currently {:ts      (tf/parse (tf/formatter :date-time-no-ms)
                                          (raw-data "cnt_date"))
                       :today   (raw-data "day_cnt")
                       :parcial (raw-data "hour_cnt")})


(defn get-last-record
  "Gets the last record from the database. It transforms the timestamp
  into Europe/Brussels time, since clj-time coerces the DateTime to be
  in UTC for some reason"
  []
  (let [record (select-last-record db)]
    (if (some? record)
      (update record :ts t/to-time-zone ,,, (t/time-zone-for-id "Europe/Brussels"))
      nil)))


(defn insert-record-policy
  "Very simple, if record does not exist already, insert it, otherwise
  do nothing. Times are in UTC (timezone +0). Brussels is CEST (+2) in
  summer and CET (+1) in november etc."
  []
  (let [last-record  (get-last-record)
        last-time    (:ts last-record)
        current-time (bikers-currently :ts)]
    ;; Insert normal records...
    (if (or (nil? last-time)
            (not= last-time current-time))
      (add-record db bikers-currently)
      "Nothing to do")
    ;; Amend hourly. (small correction to account for bikers who pass
    ;; in the last 5-10 mins of the hour). At midnight the counter is
    ;; reset to 0 so those bikers are lost, we can only update the
    ;; column with the last value seen (second clause)
    (when (some? last-time)              ;database is not empty
      (def realvalue (cond
                       ;; First clause
                       (= (inc (.getHourOfDay last-time)) (.getHourOfDay current-time)) ;its the next hour
                       (-> (:today bikers-currently)
                           (- ,,, (:today last-record))
                           (- ,,, (:parcial bikers-currently))
                           (+ ,,, (:parcial last-record)))
                       ;; Second clause
                       (and (= (.getHourOfDay last-time) 23)
                            (= (.getHourOfDay current-time) 0))
                       (:parcial last-record)))

      (amend-hourly db {:id       (:id last-record)
                        :thishour realvalue}))))


(defn -main
  [& arg]
  (insert-record-policy))
