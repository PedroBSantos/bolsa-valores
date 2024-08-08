(ns bolsa-valores.core.api.compras
  (:require [bolsa-valores.core.domain.logic :refer [calcula-preco-medio
                                                     calcula-total-comprado
                                                     codigo-acao?
                                                     pega-todas-compras-pelo-codigo-acao
                                                     calcula-total-de-acoes]]
            [bolsa-valores.core.domain.model :refer [new-compra]]
            [clojure.spec.alpha :as s])
  (:import [java.time LocalDate]
           [java.time.format DateTimeParseException]))

(s/def ::codigo-acao (s/and string? (fn [codigo-acao] (codigo-acao? codigo-acao))))
(s/def ::data-compra (s/and string? (fn [data-compra]
                                      (try
                                        (LocalDate/parse data-compra)
                                        true
                                        (catch DateTimeParseException _ false)))))
(s/def ::valor-acao (s/and number? (fn [valor] (> valor 0.0))))
(s/def ::quantidade (s/and int? (fn [quantidade] (> quantidade 0))))
(s/def ::nova-compra-spec (s/keys :req-un [::codigo-acao ::data-compra ::valor-acao ::quantidade]))

(defn- do-registra-nova-compra! [adiciona-compra! compra]
  (let [id (random-uuid)
        {codigo-acao :codigo-acao data-compra :data-compra valor-acao :valor-acao quantidade :quantidade} compra
        compra (new-compra id codigo-acao data-compra valor-acao quantidade)]
    (adiciona-compra! compra)
    {:valid :clojure.spec.alpha/valid
     :output compra}))

(defn registra-nova-compra!
  [adiciona-compra! compra]
  (if (s/valid? ::nova-compra-spec compra)
    (do-registra-nova-compra! adiciona-compra! compra)
    {:valid :clojure.spec.alpha/invalid
     :output compra}))

(defn- do-total-comprado
  ([codigo-acao todas-compras]
   (let [compras (pega-todas-compras-pelo-codigo-acao codigo-acao todas-compras)
         total-comprado (calcula-total-comprado compras)]
     {:valid :clojure.spec.alpha/valid
      :total-comprado total-comprado}))
  ([todas-compras]
   (let [total-comprado (calcula-total-comprado todas-compras)]
     {:valid :clojure.spec.alpha/valid
      :total-comprado total-comprado})))

(defn total-comprado
  ([codigo-acao todas-compras]
   (if (codigo-acao? codigo-acao)
     (do-total-comprado codigo-acao todas-compras)
     {:valid :clojure.spec.alpha/invalid
      :output {:codigo-acao codigo-acao}}))
  ([todas-compras]
   (do-total-comprado todas-compras)))

(defn- do-descrever-compras
  [codigo-acao todas-compras]
  (let [compras (pega-todas-compras-pelo-codigo-acao codigo-acao todas-compras)
        total-de-acoes (calcula-total-de-acoes compras)
        total-comprado (calcula-total-comprado compras)]
    (if (zero? total-de-acoes)
      {:valid :clojure.spec.alpha/valid
       :output {:codigo-acao codigo-acao
                :total-de-acoes 0}}
      {:valid :clojure.spec.alpha/valid
       :output {:codigo-acao codigo-acao
                :total-comprado total-comprado
                :preco-medio (calcula-preco-medio compras)
                :total-de-acoes total-de-acoes}})))

(defn descrever-compras
  [codigo-acao todas-compras]
  (if (codigo-acao? codigo-acao)
    (do-descrever-compras codigo-acao todas-compras)
    {:valid :clojure.spec.alpha/invalid
     :output {:codigo-acao codigo-acao}}))