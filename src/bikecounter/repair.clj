;; (ns bikecounter.repair
;;   (:require [environ.core :refer [env]]
;;             [bikecounter.common :refer [conn]]))

;; (defn repair-null-records-normal-fix
;;   "Usual procedure. The actual number of bikes that hour will be the
;;   partial in the previous record, plus the difference during the whole
;;   day between the record and the previous record."
;;   [arecord]
;;   (let [previous-record (get-record db {:id (dec (:id arecord))})
;;         actual-number (+ (:parcial previous-record) (- (:today arecord) (:today previous-record)))]
;;     (update-record db {:id (:id arecord)
;;                        :today (:today arecord)
;;                        :parcial actual-number
;;                        :thishour actual-number})))


;; (defn repair-null-records-next-day-fix
;;   "When the counter fails at 23:59h. Just take the previous info"
;;   [arecord]
;;   (let [previous-record (get-record db {:id (dec (:id arecord))})]
;;     (update-record db {:id (:id arecord)
;;                        :today (:today previous-record)
;;                        :parcial (:parcial previous-record)
;;                        :thishour (:parcial previous-record)})))


;; (defn repair-null-records
;;   "The insert-record-policy makes a mistake when the counter resets to 0
;;   in the 58-59th minute (the insert-record-policy will then think
;;   there have been 0 bikes that hour and update :thishour with a
;;   0!). This function fixes this."
;;   []
;;   (doseq [arecord (get-null-or-negative-records db)]
;;     (case (:today arecord)
;;       (or 0 (< 0)) (repair-null-records-next-day-fix arecord) ;at 23:59, today indicates 0
;;       (repair-null-records-normal-fix arecord))))  ;every other hour with 59 minutes


;; (defn -handler
;;   ""
;;   []
;;   (repair-null-records))
