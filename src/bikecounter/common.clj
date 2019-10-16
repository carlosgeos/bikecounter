(ns bikecounter.common
  (:require [clojure.spec.alpha :as s]
            [environ.core :refer [env]]
            [somnium.congomongo :as m])
  (:import [com.sendgrid Method Request SendGrid]
           com.sendgrid.helpers.mail.Mail
           [com.sendgrid.helpers.mail.objects Content Email]))

(def db
  (m/make-connection (env :db-string)))

(def RESOURCE_URL "http://data-mobility.brussels/geoserver/bm_bike/wfs?service=wfs&version=1.1.0&request=GetFeature&typeName=bm_bike:rt_counting&outputFormat=json")

(defn notify
  "Sends an email using SendGrid's API, saying message m"
  [m]
  (def sg (SendGrid. (env :sendgrid-api-key)))
  (def email (Mail. (Email. "bikecounter@lambda.com") ;from
                    "[bikecounter] Notification"  ;subject
                    (Email. (env :email))                 ;to
                    (Content. "text/plain" m)))

  (def req (Request.))
  (.setMethod req Method/POST)
  (.setEndpoint req "mail/send")
  (.setBody req (.build email))
  (.api sg req))
