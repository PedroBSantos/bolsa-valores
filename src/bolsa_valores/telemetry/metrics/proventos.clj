(ns bolsa-valores.telemetry.metrics.proventos
  (:require [steffan-westcott.clj-otel.api.metrics.instrument :as i]))

(defonce success-proventos-count (i/instrument {:name "bolsa-valores.success-proventos-count"
                                                :measurement-type :long
                                                :instrument-type :counter
                                                :unit "{proventos}"
                                                :description "Total de proventos recebidos com sucesso"}))

(defonce unsuccessfull-proventos-count (i/instrument {:name "bolsa-valores.unsuccess-proventos-count"
                                                      :measurement-type :long
                                                      :instrument-type :counter
                                                      :unit "{proventos}"
                                                      :description "Total de falhas durante o recebimento de proventos"}))

(defn inc-success-proventos-count [value]
  (i/add! success-proventos-count {:value value}))

(defn inc-unsuccessfull-proventos-count [value]
  (i/add! unsuccessfull-proventos-count {:value value}))