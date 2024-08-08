(ns bolsa-valores.core.api.compras-test
  (:require [bolsa-valores.core.api.compras :refer [descrever-compras
                                                    registra-nova-compra!
                                                    total-comprado]]
            [clojure.test :refer [deftest is testing use-fixtures]])
  (:import [java.time LocalDate]))

(def compras-repository (atom []))

(use-fixtures :each (fn [f]
                      (reset! compras-repository [])
                      (f)))

(deftest nova-compra-test
  (testing "Deveria gerar uma nova compra em compras-repository"
    (let [data-compra (str (LocalDate/now))
          input {:codigo-acao "VALE3" :data-compra data-compra :valor-acao 60.0 :quantidade 1}
          output (registra-nova-compra! (partial swap! compras-repository conj) input)]
      (is (= 1 (count @compras-repository)))
      (is (= :clojure.spec.alpha/valid (get output :valid :clojure.spec.alpha/invalid)))))

  (testing "Não deveria gerar uma nova compra por erros de validação"
    (let [data-compra (str (LocalDate/now))
          input {:codigo-acao "VALE3" :data-compra data-compra :valor-acao 60.0}
          output (registra-nova-compra! (partial swap! compras-repository conj) input)]
      (is (= 1 (count @compras-repository)))
      (is (= :clojure.spec.alpha/invalid (get output :valid))))))

(deftest total-comprado-test
  (testing "Deveria retornar um total de 60.0"
    (let [data-compra (str (LocalDate/now))
          ticket-acao "VALE3"
          compra {:codigo-acao "VALE3" :data-compra data-compra :valor-acao 60.0 :quantidade 1}
          _ (registra-nova-compra! (partial swap! compras-repository conj) compra)
          total (total-comprado ticket-acao @compras-repository)]
      (is (= 60.0 (get total :total-comprado 0.0)))))

  (testing "Deveria retornar 0.0"
    (let [data-compra (str (LocalDate/now))
          ticket-acao "PETR4"
          compra {:codigo-acao "VALE3" :data-compra data-compra :valor-acao 60.0 :quantidade 1}
          _ (registra-nova-compra! (partial swap! compras-repository conj) compra)
          total (total-comprado ticket-acao @compras-repository)]
      (is (= 0.0 (get total :total-comprado 0.0)))))

  (testing "Deveria retornar erro de validação"
    (let [output (total-comprado "teste" @compras-repository)]
      (is (= :clojure.spec.alpha/invalid (get output :valid))))))

(deftest descrever-compras-test
  (testing "Deveria retornar a descrição de compras de uma determinada ação"
    (let [data-compra (str (LocalDate/now))
          ticket-acao "VALE3"
          compra {:codigo-acao "VALE3" :data-compra data-compra :valor-acao 60.0 :quantidade 5}
          _ (registra-nova-compra! (partial swap! compras-repository conj) compra)
          compra {:codigo-acao "VALE3" :data-compra data-compra :valor-acao 55.0 :quantidade 5}
          _ (registra-nova-compra! (partial swap! compras-repository conj) compra)
          descricao (descrever-compras ticket-acao @compras-repository)
          esperado {:valid :clojure.spec.alpha/valid,
                    :output {:codigo-acao "VALE3",
                             :total-comprado 575.0,
                             :preco-medio 57.5,
                             :total-de-acoes 10}}]
      (is (= esperado descricao)))
    
    (let [data-compra (str (LocalDate/now))
          compra {:codigo-acao "VALE3" :data-compra data-compra :valor-acao 60.0 :quantidade 5}
          _ (registra-nova-compra! (partial swap! compras-repository conj) compra)
          compra {:codigo-acao "VALE3" :data-compra data-compra :valor-acao 55.0 :quantidade 5}
          _ (registra-nova-compra! (partial swap! compras-repository conj) compra)
          descricao (descrever-compras "PETR4" @compras-repository)
          esperado {:valid :clojure.spec.alpha/valid,
                    :output {:codigo-acao "PETR4",
                             :total-de-acoes 0}}]
      (is (= esperado descricao))))
  
  (testing "Não deveria retornar a descrição de compras quando o codigo for inválido"
    (let [data-compra (str (LocalDate/now))
          compra {:codigo-acao "VALE3" :data-compra data-compra :valor-acao 60.0 :quantidade 5}
          _ (registra-nova-compra! (partial swap! compras-repository conj) compra)
          compra {:codigo-acao "VALE3" :data-compra data-compra :valor-acao 55.0 :quantidade 5}
          _ (registra-nova-compra! (partial swap! compras-repository conj) compra)
          descricao (descrever-compras "x" @compras-repository)
          esperado {:valid :clojure.spec.alpha/invalid,
                    :output {:codigo-acao "x"}}]
      (is (= esperado descricao)))))