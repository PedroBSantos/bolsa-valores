(ns bolsa-valores.core.domain.logic-test
  (:require [bolsa-valores.core.domain.logic :refer [aplica-imposto
                                                     calcula-preco-medio
                                                     calcula-total-comprado
                                                     calcula-total-de-acoes
                                                     calcula-total-recebido
                                                     codigo-acao?
                                                     pega-todas-compras-pelo-codigo-acao
                                                     pega-todos-proventos-pelo-codigo-acao
                                                     pega-todos-proventos-recebidos-ate-data-atual
                                                     total-da-compra]]
            [bolsa-valores.core.domain.model :refer [new-compra new-provento]]
            [clojure.test :refer [deftest is testing]])
  (:import [java.time LocalDate]))

(deftest aplica-imposto-test
  (testing "Deveria calcular imposto quando provento do tipo JSCP"
    (let [id (random-uuid)
          resultado-esperado (new-provento id "PETR4" "2024-04-05" "JSCP" 0.459)
          provento (new-provento id "PETR4" "2024-04-05" "JSCP" 0.54)]
      (is (= resultado-esperado (aplica-imposto provento 0.15)))))
  (testing "Deveria retornar imposto 0.0 quando provento do tipo DIVIDENDO"
    (let [id (random-uuid)
          resultado-esperado (new-provento id "PETR4" "2024-04-05" "DIVIDENDO" 0.54)
          provento (new-provento id "PETR4" "2024-04-05" "DIVIDENDO" 0.54)]
      (is (= resultado-esperado (aplica-imposto provento 0.15))))))

(deftest total-da-compra-test
  (testing "Deveria calcular o total da compra"
    (let [id (random-uuid)
          compra (new-compra id "VALE3" (str (LocalDate/now)) 60.0 1)]
      (is (= 60.0 (total-da-compra compra))))))

(deftest codigo-acao?-test
  (testing "Deveria ser um código valido de ação"
    (let [codigo-acao-1 "VALE3"
          codigo-acao-2 "PETR4"
          codigo-acao-3 "TAEE11"]
      (is (codigo-acao? codigo-acao-1))
      (is (codigo-acao? codigo-acao-2))
      (is (codigo-acao? codigo-acao-3))))
  (testing "Não deveria ser um código valido de ação"
    (let [codigo-acao-1 "vale3"
          codigo-acao-2 "petr4"
          codigo-acao-3 "xpto20"]
      (is (not (codigo-acao? codigo-acao-1)))
      (is (not (codigo-acao? codigo-acao-2)))
      (is (not (codigo-acao? codigo-acao-3))))))

(deftest pega-todas-compras-pelo-codigo-acao-test
  (testing "Deveria filtar todas as compras com um determinado ticket"
    (let [codigo-acao "VALE3"
          compra-1 (new-compra (random-uuid) "VALE3" (str (LocalDate/now)) 60.0 1)
          compra-2 (new-compra (random-uuid) "VALE3" (str (LocalDate/now)) 60.0 1)
          compra-3 (new-compra (random-uuid) "PETR4" (str (LocalDate/now)) 41.20 1)
          compras [compra-1 compra-2 compra-3]]
      (is (= [compra-1 compra-2] (pega-todas-compras-pelo-codigo-acao codigo-acao compras)))))
  (testing "Não deveria retornar nenhuma ação quando nenhuma compra for realizada"
    (let [codigo-acao "VALE3"
          compras []]
      (is (empty? (pega-todas-compras-pelo-codigo-acao codigo-acao compras))))))

(deftest calcula-total-comprado-test
  (testing "Deveria calcular o total gasto em uma ação específica"
    (let [codigo-acao "VALE3"
          compra-1 (new-compra (random-uuid) "VALE3" (str (LocalDate/now)) 60.0 1)
          compra-2 (new-compra (random-uuid) "VALE3" (str (LocalDate/now)) 60.0 1)
          compra-3 (new-compra (random-uuid) "PETR4" (str (LocalDate/now)) 41.20 1)
          compras [compra-1 compra-2 compra-3]
          todas-as-compras (pega-todas-compras-pelo-codigo-acao codigo-acao compras)]
      (is (= 120.0 (calcula-total-comprado todas-as-compras)))))
  (testing "Deveria retornar 0.0 quando a ação não foi comprada"
    (let [codigo-acao "PETR4"
          compra-1 (new-compra (random-uuid) "VALE3" (str (LocalDate/now)) 60.0 1)
          compra-2 (new-compra (random-uuid) "VALE3" (str (LocalDate/now)) 60.0 1)
          compras [compra-1 compra-2]
          todas-as-compras (pega-todas-compras-pelo-codigo-acao codigo-acao compras)]
      (is (= 0.0 (calcula-total-comprado todas-as-compras))))))

(deftest pega-todos-proventos-pelo-codigo-acao-test
  (testing "Deveria filtar todos os proventos com um determinado ticket"
    (let [codigo-acao "PETR4"
          provento-1 (new-provento (random-uuid) "PETR4" "2024-04-05" "JSCP" 0.54)
          provento-2 (new-provento (random-uuid) "PETR4" "2024-06-05" "DIVIDENDO" 0.54)
          provento-3 (new-provento (random-uuid) "VALE3" "2024-06-05" "DIVIDENDO" 2.70)
          proventos [provento-1 provento-2 provento-3]]
      (is (= [provento-1 provento-2] (pega-todos-proventos-pelo-codigo-acao codigo-acao proventos)))))
  (testing "Não deveria retornar nenhum provento quando nenhum recebido"
    (let [codigo-acao "VALE3"
          proventos []]
      (is (empty? (pega-todos-proventos-pelo-codigo-acao codigo-acao proventos))))))

(deftest pega-todos-proventos-recebidos-ate-data-atual-test
  (testing "Deveria filtar todos os proventos recebidos até a data atual"
    (let [data-atual (LocalDate/now)
          amanha (.plusDays data-atual 1)
          provento-1 (new-provento (random-uuid) "PETR4" (str data-atual) "JSCP" 0.54)
          provento-2 (new-provento (random-uuid) "PETR4" (str data-atual) "DIVIDENDO" 0.54)
          provento-3 (new-provento (random-uuid) "VALE3" (str amanha) "DIVIDENDO" 2.70)
          proventos [provento-1 provento-2 provento-3]]
      (is (= [provento-1 provento-2] (pega-todos-proventos-recebidos-ate-data-atual proventos)))))
  (testing "Não deveria retornar nenhum provento se todos serão recebidos depois da data atual"
    (let [data-atual (LocalDate/now)
          amanha (.plusDays data-atual 1) 
          provento-1 (new-provento (random-uuid) "PETR4" (str amanha) "JSCP" 0.54)
          provento-2 (new-provento (random-uuid) "PETR4" (str amanha) "DIVIDENDO" 0.54)
          provento-3 (new-provento (random-uuid) "VALE3" (str amanha) "DIVIDENDO" 2.70)
          proventos [provento-1 provento-2 provento-3]]
      (is (= [] (pega-todos-proventos-recebidos-ate-data-atual proventos))))))

(deftest calcula-total-recebido-test
  (testing "Deveria retornar o total recebido em proventos de uma ação independente da data de pagamento"
    (let [data-atual (LocalDate/now)
          amanha (.plusDays data-atual 1) 
          provento-1 (new-provento (random-uuid) "PETR4" (str data-atual) "JSCP" 0.54)
          provento-2 (new-provento (random-uuid) "PETR4" (str amanha) "DIVIDENDO" 0.54)
          proventos [provento-1 provento-2]]
      (is (= 1.08 (calcula-total-recebido proventos))))))

(deftest calcula-total-de-acoes-test
  (testing "Deveria retornar o total de ações compradas"
    (let [compra-1 (new-compra (random-uuid) "VALE3" (str (LocalDate/now)) 60.0 1)
          compra-2 (new-compra (random-uuid) "VALE3" (str (LocalDate/now)) 60.0 1)
          compra-3 (new-compra (random-uuid) "PETR4" (str (LocalDate/now)) 41.20 1)
          compras [compra-1 compra-2 compra-3]]
      (is (= 3 (calcula-total-de-acoes compras)))
      (is (zero? (calcula-total-de-acoes []))))))

(deftest calcula-preco-medio-test
  (testing "Deveria retornar o preço médio de uma ação"
    (let [compra-1 (new-compra (random-uuid) "GOAU4" (str (LocalDate/now)) 9.94 40)
          compra-2 (new-compra (random-uuid) "GOAU4" (str (LocalDate/now)) 10.0 40)]
      (is (= 9.969999999999999 (calcula-preco-medio [compra-1 compra-2]))))))