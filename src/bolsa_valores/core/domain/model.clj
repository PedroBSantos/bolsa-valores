(ns bolsa-valores.core.domain.model
  (:import [java.util UUID]))

(defrecord Compra [id codigo-acao data-compra valor-acao quantidade])
(defn new-compra [^UUID id ^String codigo-acao ^String data-compra ^Double valor-acao ^Long quantidade]
  (->Compra id codigo-acao data-compra valor-acao quantidade))

(defrecord Provento [id codigo-acao data-pagamento tipo valor])
(defn new-provento [^UUID id ^String codigo-acao ^String data-pagamento ^String tipo ^Double valor]
  (->Provento id codigo-acao data-pagamento tipo valor))
