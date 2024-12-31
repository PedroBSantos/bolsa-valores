(ns bolsa-valores.core.api.proventos
  (:require [bolsa-valores.core.domain.logic :refer [aplica-imposto
                                                     calcula-total-recebido
                                                     codigo-acao?
                                                     pega-proventos-pelo-tipo
                                                     pega-todos-proventos-pelo-codigo-acao
                                                     pega-todos-proventos-recebidos-ate-data-atual]]
            [bolsa-valores.core.domain.model :refer [new-provento]]
            [clojure.spec.alpha :as s]
            [clojure.tools.logging :as l])
  (:import [java.time LocalDate]
           [java.time.format DateTimeParseException]))

(s/def ::codigo-acao (s/and string? (fn [codigo-acao] (codigo-acao? codigo-acao))))
(s/def ::tipo (s/and string? (fn [tipo] (or (= "JSCP" (.toUpperCase tipo)) (= "DIVIDENDO" (.toUpperCase tipo))))))
(s/def ::valor (s/and number? (fn [valor] (> valor 0.0))))
(s/def ::data-pagamento (s/and string? (fn [data-pagamento]
                                         (try
                                           (LocalDate/parse data-pagamento)
                                           true
                                           (catch DateTimeParseException _ false)))))
(s/def ::taxa-desconto (s/and number? (fn [taxa] (and (>= taxa 0.0) (<= taxa 1.0)))))
(s/def ::novo-provento-spec (s/keys :req-un [::codigo-acao ::data-pagamento ::tipo ::valor ::taxa-desconto]))

(defn- do-registra-novo-provento! [adiciona-provento! provento]
  (let [id (random-uuid)
        {codigo-acao :codigo-acao data-pagamento :data-pagamento tipo :tipo valor :valor taxa-desconto :taxa-desconto} provento
        provento (new-provento id codigo-acao data-pagamento tipo valor)
        provento-descontado (aplica-imposto provento taxa-desconto)]
    (adiciona-provento! provento-descontado)
    {:valid :clojure.spec.alpha/valid
     :output provento-descontado}))

(defn registra-novo-provento!
  [adiciona-provento! provento]
  (if (s/valid? ::novo-provento-spec provento)
    (do-registra-novo-provento! adiciona-provento! provento)
    (do
      (l/warn "Erro ao registrar provento")
      {:valid :clojure.spec.alpha/invalid
     :output provento})))

(defn- do-total-recebido 
  ([codigo-acao todos-proventos]
  (let [proventos-da-acao (pega-todos-proventos-pelo-codigo-acao codigo-acao todos-proventos)
        proventos-da-acao (pega-todos-proventos-recebidos-ate-data-atual proventos-da-acao)
        total (calcula-total-recebido proventos-da-acao)]
    {:valid :clojure.spec.alpha/valid
     :total-recebido total}))
  ([todos-proventos]
   (let [total (calcula-total-recebido todos-proventos)]
     {:valid :clojure.spec.alpha/valid
      :total-recebido total})))

(defn total-recebido 
  ([codigo-acao todos-proventos]
  (if (codigo-acao? codigo-acao)
    (do-total-recebido codigo-acao todos-proventos)
    {:valid :clojure.spec.alpha/invalid
     :output {:codigo-acao codigo-acao}}))
  ([todos-proventos]
   (do-total-recebido todos-proventos)))

(defn- do-descrever-proventos 
  [codigo-acao todos-proventos]
  (let [proventos (pega-todos-proventos-pelo-codigo-acao codigo-acao todos-proventos)
        total-recebido (calcula-total-recebido proventos)
        dividendos (pega-proventos-pelo-tipo "DIVIDENDO" proventos)
        jscps (pega-proventos-pelo-tipo "JSCP" proventos)
        total-recebido-dividendos (calcula-total-recebido dividendos)
        total-recebido-jscp (calcula-total-recebido jscps)]
    {:valid :clojure.spec.alpha/valid
     :output {:codigo-acao codigo-acao
              :total-recebido total-recebido
              :total-em-dividendos total-recebido-dividendos
              :total-em-jscp total-recebido-jscp}}))

(defn descrever-proventos 
  [codigo-acao todos-proventos]
  (if (codigo-acao? codigo-acao)
    (do-descrever-proventos codigo-acao todos-proventos)
    {:valid :clojure.spec.alpha/invalid
     :output {:codigo-acao codigo-acao}}))