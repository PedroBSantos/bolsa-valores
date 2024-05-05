(ns bolsa-valores.core.domain.logic
  (:import [java.time LocalDate]))

(defn total-da-compra [compra]
  (let [quantidade (get compra :quantidade)
        valor-acao (get compra :valor-acao)]
    (* quantidade valor-acao)))

(defn codigo-acao? [codigo-acao]
  (let [regex #"[A-Z]{4}(3|4|11)"]
    (not= nil (re-matches regex codigo-acao))))

(defn dividendo? [provento]
  (-> provento
      (get :tipo)
      (= "DIVIDENDO")))

(defn juros-sobre-capital-proprio? [provento]
  (-> provento
      (get :tipo)
      (= "JSCP")))

(defmulti gera-funcao-desconto juros-sobre-capital-proprio?)

(defmethod gera-funcao-desconto false [_] (fn [valor _] valor))

(defmethod gera-funcao-desconto true [_]
  (fn [valor taxa] (->> valor
                        (* taxa)
                        (- valor))))

(defn aplica-imposto [provento taxa]
  (let [funcao-desconto (gera-funcao-desconto provento)]
    (update provento
            :valor
            (fn [valor] (funcao-desconto valor taxa)))))

(defn pega-todos-proventos-pelo-codigo-acao [codigo-acao todos-proventos]
  (filter
   (fn [provento] (= codigo-acao (get provento :codigo-acao "")))
   todos-proventos))

(defn pega-todos-proventos-recebidos-ate-data-atual [todos-proventos]
  (filter
   (fn [provento]
     (let [data-atual (LocalDate/now)
           data-pagamento (get provento :data-pagamento data-atual)]
       (not (.isAfter (LocalDate/parse data-pagamento) data-atual))))
   todos-proventos))

(defn calcula-total-recebido [proventos]
  (reduce
   (fn [acumulado atual] (+ acumulado (get atual :valor 0.0)))
   0.0
   proventos))

(defn pega-todas-compras-pelo-codigo-acao [codigo-acao todas-compras]
  (filter
   (fn [compra] (= codigo-acao (get compra :codigo-acao "")))
   todas-compras))

(defn- calcula-subtotal-comprado [compra]
  (* (get compra :quantidade) (get compra :valor-acao)))

(defn calcula-total-comprado [compras]
  (reduce
   (fn [acumulado atual] (+ acumulado (calcula-subtotal-comprado atual)))
   0.0
   compras))