(ns bikecounter.fetch
  (:gen-class
   :methods [^:static [handler [Object] Object]])
  (:require [bikecounter.common :refer [conn lambda_default sg-api-token]]
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
   :numberMatched s/Int
   :numberReturned s/Int
   :timeStamp s/Str
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
                            :cnt_time (s/maybe s/Str)}
               :bbox [s/Num]}]
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


(defn not-recently-notified
  "Returns true if the notification recipient hasn't received a
  notification in the the past hours hours"
  [hours]
  (let [last-notified (:last-notification(m/with-mongo conn
                                           (m/fetch-one :notification)))]
    (< hours (t/as (t/duration last-notified (t/instant)) :hours))))


(defn not-ok
  "Send a notification email, prints the error and crash the process"
  [error]
  (when (not-recently-notified 1)
    (sg/send-email {:api-token sg-api-token
                    :from "notification@bikecounter.io"
                    :to (str (env :notification-recipient))
                    :subject "Bikecounter error"
                    :message (f/message error)})
    (m/with-mongo conn
      (m/update! :notification {:only-one true} {:$set {:last-notification (t/instant)}})))
  (println (f/message error))
  (throw (Exception. "Error")))


(defn save-data
  "Saves the data fetched from the API to Mongo. feature is a map
  containing info about the counting device (times, state, name, etc)"
  [data]
  (doseq [feature (:features data)]
    (m/with-mongo conn
      (m/insert! (keyword (:device_name (:properties feature))) feature))))


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


(defn -handler
  "Fetches data from the API and stores the record in a database. Libraries
  are all the different URL endpoints from which to fetch data"
  [req]
  (process api-url)
  (merge lambda_default {"body" (json/generate-string "OK")}))
