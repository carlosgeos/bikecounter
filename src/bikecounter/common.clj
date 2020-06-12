(ns bikecounter.common
  (:require [environ.core :refer [env]]
            [somnium.congomongo :as m]))

(def lambda_default
  "AWS Lambda stuff... It is the answer template for AWS API Gateway"
  {"isBase64Encoded" false
   "headers" {}
   "statusCode" 200})

(def conn
  (m/make-connection (env :database-url)))

(def sg-api-token
  (str "Bearer " (env :sendgrid-api-token)))
