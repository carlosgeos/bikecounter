(ns bikecounter.spec
  (:require [clojure.spec.alpha :as s]))

(s/def :prop/road_en (s/nilable string?))
(s/def :prop/hour_cnt (s/nilable int?))
(s/def :prop/day_cnt (s/nilable int?))
(s/def :prop/year_cnt (s/nilable int?))
(s/def :prop/cnt_time (s/nilable string?))
(s/def :property/id int?)
(s/def :feature/properties
  (s/keys :req-un [::road_en ::hour_cnt ::day_cnt ::year_cnt ::cnt_time]
          :opt-un [::device_name :property/id]))
(s/def :feature/id int?)
(s/def :feature/type #{"Feature"})
(s/def :root/type #{"FeatureCollection"})
(s/def :root/totalFeatures pos-int?)
(s/def :root/features (s/coll-of (s/keys :req-un [::properties]
                                         :opt-un [:feature/id :feature/type])))
(s/def ::geojson
  (s/keys :req-un [:root/type :root/totalFeatures :root/features]))
