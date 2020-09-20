(ns bikecounter.core
  (:gen-class
   :methods [^:static [handler [Object] Object]])
  (:require [bikecounter.common :refer [conn lambda_default]]
            [cheshire.core :as json]
            [java-time :as t]
            [somnium.congomongo :as m]))

(defn get-bikes
  ""
  [start end counter]
  (:result (m/with-mongo conn
             (m/aggregate
              (keyword counter)
              {:$match {:properties.cnt_time {:$exists true
                                              :$ne nil
                                              :$gt (t/local-date "yyyy-MM-dd" start)
                                              :$lte (t/local-date "yyyy-MM-dd" end)}}}
              {:$project {:properties {:hour_cnt 1
                                       :cnt_time 1
                                       ;; Timezone can be specified for the hour
                                       :hour {:$hour :$properties.cnt_time}
                                       :day {:$dayOfMonth :$properties.cnt_time}
                                       :month {:$month :$properties.cnt_time}
                                       :year {:$year :$properties.cnt_time}}}}
              {:$group {:_id {:hour :$properties.hour
                              :day :$properties.day
                              :month :$properties.month
                              :year :$properties.year}
                        :hour_cnt {:$max :$properties.hour_cnt}
                        :cnt_time {:$first :$properties.cnt_time}}}
              {:$project {:_id 0
                          :x :$cnt_time
                          :y :$hour_cnt}}
              {:$sort {:cnt_time 1}}))))


(defn -handler
  "API Gateway transforms query GET params into POST fields, which can
  be found in s.queryStringParameters"
  [s]
  (let [input (get s "queryStringParameters")
        start (get input "start")
        end (get input "end")
        counter (get input "counter")]
    (merge lambda_default {"headers" {"Access-Control-Allow-Origin" "*"
                                      "Access-Control-Allow-Headers" "Content-Type"}
                           "body" (json/generate-string (get-bikes start end counter))})))
