(ns bolsa-valores.telemetry.metrics.compras
  (:require [steffan-westcott.clj-otel.api.metrics.instrument :as i]))

(defonce success-compras-count (i/instrument {:name "bolsa-valores.success-compras-count"
                                              :measurement-type :long
                                              :instrument-type :counter
                                              :unit "{compras}"
                                              :description "Total de compras de ações realizadas com sucesso"}))

(defonce unsuccessfull-compras-count (i/instrument {:name "bolsa-valores.unsuccess-compras-count"
                                                    :measurement-type :long
                                                    :instrument-type :counter
                                                    :unit "{compras}"
                                                    :description "Total de compras de ações que falharam"}))

(defn inc-success-compras-count [value]
  (i/add! success-compras-count {:value value}))

(defn inc-unsuccessfull-compras-count [value]
  (i/add! unsuccessfull-compras-count {:value value}))