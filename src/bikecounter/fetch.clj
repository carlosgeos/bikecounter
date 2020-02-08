(ns bikecounter.fetch
  (:gen-class
   :methods [^:static [handler [Object] Object]])
  (:require [bikecounter.common :refer [conn sg-api-token]]
            [cheshire.core :as json]
            [clj-http.client :as client]
            [environ.core :refer [env]]
            [failjure.core :as f]
            [java-time :as t]
            [schema.core :as s]
            [sendgrid.core :as sg]
            [somnium.congomongo :as m]))

(def api-url "http://data-mobility.brussels/geoserver/bm_bike/wfs?service=wfs&version=1.1.0&request=GetFeature&typeName=bm_bike:rt_counting&outputFormat=json")

(def data-shape
  "A schema for the Brussels Open Data API"
  {:type s/Str
   :totalFeatures s/Int
   :features [{:type s/Str
               :id s/Str
               :geometry {:type s/Str
                          :coordinates [s/Num]}
               :geometry_name s/Str
               :properties {:id s/Int
                            :device_name s/Str
                            :road_nl s/Str
                            :road_fr s/Str
                            :road_en s/Str
                            :descr_nl s/Str
                            :descr_fr s/Str
                            :descr_en s/Str
                            :lane_schema s/Str
                            :basic_schema s/Str
                            :detailed_schema s/Str
                            :hour_cnt (s/maybe s/Int)
                            :day_cnt (s/maybe s/Int)
                            :year_cnt (s/maybe s/Int)
                            :cnt_time (s/maybe s/Str)
                            :bbox [s/Num]}}]
   :crs {:type s/Str
         :properties {:name s/Str}}
   :bbox [s/Num]})


(defn fetch-api
  "Calls the external resource and returns the result. If an Exception
  occurs, Failjure lib catches it and treats it as a failure"
  [url]
  (f/try* (:body (client/get url {:as :json}))))


(defn add-timestamps
  "Convert String to Timestamp"
  [feature]
  (let [ts (:cnt_time (:properties feature))]
    (if (not (nil? ts))
      (assoc-in feature [:properties :cnt_time] (t/instant ts))
      feature)))


(defn transform-data
  [data]
  (assoc data :features (map add-timestamps (:features data))))


(defn validate-data
  "Makes sure Brussels Open Data have not changed their API"
  [data]
  (try
    (s/validate data-shape data)
    (catch Exception e
      (f/fail "Data is not valid ! Schema returned error: %s" e))))


(defn not-ok
  "Send a notification email, prints the error and crash the process"
  [error]
  (sg/send-email {:api-token sg-api-token
                  :from "notification@bikecounter.io"
                  :to (str (env :notification-recipient))
                  :subject "Bikecounter error"
                  :message (f/message error)})
  (println (f/message error))
  (throw (Exception. "Error")))


(defn save-data
  "Saves the data fetched from the API to Mongo. feature is a map
  containing info about the counting device (times, state, name, etc)"
  [data]
  (doseq [feature (:features data)]
    (m/with-mongo conn
      (m/insert! (keyword (:device_name (:properties feature))) feature))))


;; (def bikers-currently {:ts      (tf/parse (tf/formatter :date-time-no-ms)
;;                                           (raw-data "cnt_time"))
;;                        :today   (raw-data "day_cnt")
;;                        :parcial (raw-data "hour_cnt")})


;; (defn insert-record-policy
;;   "Very simple, if record does not exist already, insert it, otherwise
;;   do nothing. Times are in UTC (timezone +0). Brussels is CEST (+2) in
;;   summer and CET (+1) as from november etc."
;;   []
;;   (let [last-record  (select-last-record conn)
;;         last-time    (:ts last-record)
;;         current-time (bikers-currently :ts)]
;;     ;; Insert normal records...
;;     (if (or (nil? last-time)
;;             (not= last-time current-time))
;;       (add-record conn bikers-currently)
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

;;       (amend-hourly conn {:id       (:id last-record)
;;                         :thishour realvalue}))))


(defn process
  "Puts the data through a series of transformations, ending in the
  saving of it. If there is an error at some point, the procedure
  shotcuts to the not-ok procedure (failjure feature)"
  [api-url]
  (let [result (f/ok->> api-url
                        (fetch-api)
                        (validate-data)
                        (transform-data)
                        (save-data))]
    (if (f/failed? result)
      (not-ok result)
      result)))


(def lambda_default
  "AWS Lambda stuff... It is the answer template for AWS API Gateway"
  {"isBase64Encoded" false
   "headers" {}
   "statusCode" 200})


(defn -handler
  "Fetches data from the API and stores the record in a database. Libraries
  are all the different URL endpoints from which to fetch data"
  [req]
  (process api-url)
  (merge lambda_default {"body" (json/generate-string "OK")}))
