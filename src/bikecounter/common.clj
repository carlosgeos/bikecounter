(ns bikecounter.common
  (:require [environ.core :refer [env]]
            [somnium.congomongo :as m]))

(def conn
  (m/make-connection (env :database-url)))

(def sg-api-token
  (str "Bearer " (env :sendgrid-api-token)))
