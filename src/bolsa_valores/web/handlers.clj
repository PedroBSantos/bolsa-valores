(ns bolsa-valores.web.handlers
  (:require [bolsa-valores.core.api.compras :refer [descrever-compras
                                                    registra-nova-compra!
                                                    total-comprado]]
            [bolsa-valores.core.api.proventos :refer [descrever-proventos
                                                      registra-novo-provento!
                                                      total-recebido]]
            [bolsa-valores.telemetry.metrics.compras :refer [inc-success-compras-count inc-unsuccessfull-compras-count]]
            [bolsa-valores.telemetry.metrics.proventos :refer [inc-success-proventos-count inc-unsuccessfull-proventos-count]]
            [bolsa-valores.database.queries :as q]
            [ring.util.response :refer [bad-request created]]))

(defn novo-provento-handler [request]
  (let [request-body (get request :body {})
        novo-provento (registra-novo-provento! q/insert-provento request-body)
        response-body (dissoc novo-provento :valid)
        request-uri (get request :uri)]
    (if (= :clojure.spec.alpha/valid (:valid novo-provento))
      (do
        (inc-success-proventos-count 1)
        (created (str request-uri "/" (get-in response-body [:output :id])) response-body))
      (do
        (inc-unsuccessfull-proventos-count 1)
        (bad-request response-body)))))

(defn descrever-proventos-handler [request]
  (let [codigo-acao (get-in request [:query-params "codigo-acao"] "")
        todos-proventos (q/read-all-proventos)
        descricao (descrever-proventos codigo-acao todos-proventos)
        response-body (dissoc descricao :valid)]
    (if (= :clojure.spec.alpha/valid (:valid descricao))
      {:status 200 :body response-body}
      (bad-request response-body))))

(defn proventos-handler [_]
  (let [todas-proventos (q/read-all-proventos)]
    {:status 200
     :body todas-proventos}))

(defn nova-compra-handler [request]
  (let [request-body (get request :body {})
        nova-compra (registra-nova-compra! q/insert-compra request-body)
        response-body (dissoc nova-compra :valid)
        request-uri (get request :uri)]
    (if (= :clojure.spec.alpha/valid (:valid nova-compra))
      (do
        (inc-success-compras-count 1)
        (created (str request-uri "/" (get-in response-body [:output :id])) response-body))
      (do
        (inc-unsuccessfull-compras-count 1)
        (bad-request response-body)))))

(defn descrever-compras-handler [request]
  (let [codigo-acao (get-in request [:query-params "codigo-acao"] "")
        todas-compras (q/read-all-compras)
        descricao (descrever-compras codigo-acao todas-compras)
        response-body (dissoc descricao :valid)]
    (if (= :clojure.spec.alpha/valid (:valid descricao))
      {:status 200 :body response-body}
      (bad-request response-body))))

(defn compras-handler [_]
  (let [todas-compras (q/read-all-compras)]
    {:status 200
     :body todas-compras}))

(defn total-comprado-na-acao-handler [request]
  (let [ticket-acao (.toUpperCase (get-in request [:query-params "codigo-acao"] ""))
        todas-compras (q/read-all-compras)
        total (total-comprado ticket-acao todas-compras)
        response-body (dissoc total :valid)]
    (if (= :clojure.spec.alpha/valid (:valid total))
      {:status 200
       :body response-body}
      (bad-request response-body))))

(defn total-comprado-handler [_]
  (let [todas-compras (q/read-all-compras)
        total (total-comprado todas-compras)
        response-body (dissoc total :valid)]
    {:status 200
     :body response-body}))

(defn total-recebido-na-acao-handler [request]
  (let [ticket-acao (.toUpperCase (get-in request [:query-params "codigo-acao"] ""))
        todos-proventos (q/read-all-proventos)
        total (total-recebido ticket-acao todos-proventos)
        response-body (dissoc total :valid)]
    (if (= :clojure.spec.alpha/valid (:valid total))
      {:status 200
       :body response-body}
      (bad-request response-body))))

(defn total-recebido-handler [_]
  (let [todos-proventos (q/read-all-proventos)
        total (total-recebido todos-proventos)
        response-body (dissoc total :valid)]
    {:status 200
     :body response-body}))