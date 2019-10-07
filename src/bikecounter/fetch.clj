(ns bikecounter.fetch
  (:gen-class
   :methods [^:static [handler [String] String]])
  (:require [bikecounter.common :refer [db]]
            [cheshire.core :as json]
            [clj-http.client :as requests]
            [clojure.java.jdbc :as j]
            [clojure.spec.alpha :as s]
            [environ.core :refer [env]]
            [hugsql.core :as hugsql])
  (:import [com.sendgrid SendGrid Request Method]
           com.sendgrid.helpers.mail.Mail
           [com.sendgrid.helpers.mail.objects Content Email]))

;; ;; The path is relative to the classpath (not proj dir!), so "src" is
;; ;; not included in the path.  The same would apply if the sql was
;; ;; under "resources/..."  Also, notice the under_scored path compliant
;; ;; with Clojure file paths for hyphenated namespaces
(hugsql/def-db-fns "sql/queries.sql")
(hugsql/def-sqlvec-fns "sql/queries.sql")

(def RESOURCE_URL "http://data-mobility.brussels/geoserver/bm_bike/wfs?service=wfs&version=1.1.0&request=GetFeature&typeName=bm_bike:rt_counting&outputFormat=json")

(defn notify-of-failure
  "Sends an email using SendGrid's API, saying that something went wrong
  with the bike counter resource"
  []
  (def sg (SendGrid. (env :sendgrid-api-key)))
  (def email (Mail. (Email. "bikecounter@lambda.com") ;from
                    "[bikecounter] Failure notification"  ;subject
                    (Email. (env :email))                 ;to
                    (Content. "text/plain" "Cron job failed to fetch the remote GeoJSON resource")))

  (def req (Request.))
  (.setMethod req Method/POST)
  (.setEndpoint req "mail/send")
  (.setBody req (.build email))
  (.api sg req))


(defn fetch-data
  ""
  []
  (json/parse-string (:body (requests/get RESOURCE_URL)) true))

(def result (fetch-data))

(s/def :property/id int?)
(s/def ::properties
  (s/keys :req-un [::road_en ::hour_cnt ::day_cnt ::year_cnt ::cnt_time]
          :opt-un [::device_name :property/id]))
(s/def :feature/id string?)
(s/def :feature/type #{"Feature"})
(s/def ::feature
  (s/keys :req-un [::properties]
          :opt-un [:feature/id :feature/type]))
(s/def :root/type #{"FeatureCollection"})
(s/def ::totalFeatures pos-int?)
(s/def ::features (s/coll-of ::feature))
(s/def ::geojson-spec
  (s/keys :req-un [:root/type ::totalFeatures ::features]))

(when (s/valid? ::geojson-spec result)
  (notify-of-failure))

;; (def bikers-currently {:ts      (tf/parse (tf/formatter :date-time-no-ms)
;;                                           (raw-data "cnt_time"))
;;                        :today   (raw-data "day_cnt")
;;                        :parcial (raw-data "hour_cnt")})


;; (defn insert-record-policy
;;   "Very simple, if record does not exist already, insert it, otherwise
;;   do nothing. Times are in UTC (timezone +0). Brussels is CEST (+2) in
;;   summer and CET (+1) in november etc."
;;   []
;;   (let [last-record  (select-last-record db)
;;         last-time    (:ts last-record)
;;         current-time (bikers-currently :ts)]
;;     ;; Insert normal records...
;;     (if (or (nil? last-time)
;;             (not= last-time current-time))
;;       (add-record db bikers-currently)
;;       "Nothing to do")
;;     ;; Amend hourly. At midnight the counter is reset to 0 so those
;;     ;; bikers are lost, we can only update the column with the last value
;;     ;; seen (first clause). Also, there is a small correction to account
;;     ;; for bikers who pass in the last 5 mins of the hour - second
;;     ;; clause.
;;     (when (some? last-time)              ;database is not empty
;;       (def realvalue (cond
;;                        ;; First clause. Order is important
;;                        (and (= (.getHourOfDay last-time) 22)
;;                             (= (.getHourOfDay current-time) 23))
;;                        (:parcial last-record)
;;                        ;; Second clause
;;                        (= (inc (.getHourOfDay last-time)) (.getHourOfDay current-time)) ;its the next hour
;;                        (-> (:today bikers-currently)
;;                            (- ,,, (:today last-record) (:parcial bikers-currently))
;;                            (+ ,,, (:parcial last-record)))))

;;       (amend-hourly db {:id       (:id last-record)
;;                         :thishour realvalue}))))



;; (defn -main
;;   [& arg]
;;   (insert-record-policy)))
