(ns bolsa-valores.web.handlers
  (:require [bolsa-valores.core.api.compras :refer [registra-nova-compra!
                                                    total-comprado]]
            [bolsa-valores.core.api.proventos :refer [registra-novo-provento!
                                                      total-recebido]]
            [bolsa-valores.database.queries :as q]
            [ring.util.response :refer [bad-request created]]))

(defn novo-provento-handler [request]
  (let [request-body (get request :body {})
        novo-provento (registra-novo-provento! q/insert-provento request-body)
        response (dissoc novo-provento :valid)
        request-uri (get request :uri)]
    (if (= :clojure.spec.alpha/valid (:valid novo-provento))
      (created (str request-uri "/" (get-in response [:output :id])) response)
      (bad-request response))))

(defn nova-compra-handler [request]
  (let [request-body (get request :body {})
        nova-compra (registra-nova-compra! q/insert-compra request-body)
        response (dissoc nova-compra :valid)
        request-uri (get request :uri)]
    (if (= :clojure.spec.alpha/valid (:valid nova-compra))
      (created (str request-uri "/" (get-in response [:output :id])) response)
      (bad-request response))))

(defn total-comprado-handler [request]
  (let [ticket-acao (.toUpperCase (get-in request [:query-params "codigo-acao"] ""))
        todas-compras (q/read-all-compras)
        total (total-comprado ticket-acao todas-compras)
        response (dissoc total :valid)]
    (if (= :clojure.spec.alpha/valid (:valid total))
      {:status 200
       :body response}
      (bad-request response))))

(defn total-recebido-handler [request]
  (let [ticket-acao (.toUpperCase (get-in request [:query-params "codigo-acao"] ""))
        todos-proventos (q/read-all-proventos)
        total (total-recebido ticket-acao todos-proventos)
        response (dissoc total :valid)]
    (if (= :clojure.spec.alpha/valid (:valid total))
      {:status 200
       :body response}
      (bad-request response))))