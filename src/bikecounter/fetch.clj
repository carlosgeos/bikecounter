(ns bikecounter.fetch
  (:gen-class
   :methods [^:static [handler [String] String]])
  (:require [bikecounter.common :refer [db notify RESOURCE_URL]]
            [cheshire.core :as json]
            [clj-http.client :as requests]
            [clojure.spec.alpha :as s]
            [environ.core :refer [env]]
            [java-time :as t]
            [somnium.congomongo :as m]))

(defn fetch-data
  ""
  []
  (try
    (json/parse-string (:body (requests/get RESOURCE_URL)) true)
    (catch Exception e
      (notify "Could not fetch resource. API down ?")
      (str "Caught exception: " (.getMessage e)))))

(defn insert-in-db
  "Straightforward inserting in MongoDB"
  [data]
  (doseq [feature-api (:features data)]
    (m/with-mongo db
      (let [props (:properties feature-api)
            coords (:coordinates (:geometry feature-api))]
        (m/insert! :bikecounter
                   {:device_name (:device_name props)
                    :road_en (:road_en props)
                    :coordinates coords
                    :hour_cnt (:hour_cnt props)
                    :day_cnt (:day_cnt props)
                    :year_cnt (:year_cnt props)
                    :cnt_time (t/local-date-time (t/zoned-date-time (or (:cnt_time props) 2010)))
                    :updatedAt (t/local-date-time)})))))

;; (def bikers-currently {:ts      (tf/parse (tf/formatter :date-time-no-ms)
;;                                           (raw-data "cnt_time"))
;;                        :today   (raw-data "day_cnt")
;;                        :parcial (raw-data "hour_cnt")})


;; (defn insert-record-policy
;;   "Very simple, if record does not exist already, insert it, otherwise
;;   do nothing. Times are in UTC (timezone +0). Brussels is CEST (+2) in
;;   summer and CET (+1) as from november etc."
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



(defn -main
  [& arg]
  (let [data (fetch-data)]
    (if (not (s/valid? :bikecounter.spec/geojson data))
      (notify (str "GeoJSON resource does not conform to spec\n\n"
                   (s/explain-str :bikecounter.spec/geojson data)))
      (insert-in-db data))))
