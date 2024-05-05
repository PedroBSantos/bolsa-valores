(ns bolsa-valores.core.api.proventos-test
  (:require [bolsa-valores.core.api.proventos :refer [registra-novo-provento!
                                                      total-recebido]]
            [bolsa-valores.core.domain.model :refer [new-provento]]
            [clojure.test :refer [deftest is testing use-fixtures]])
  (:import [java.time LocalDate]))

(def proventos-repository (atom []))

(use-fixtures :each (fn [f]
                      (reset! proventos-repository [])
                      (f)))

(deftest novo-provento-test
  (testing "Deveria gerar um novo provento em proventos-repository"
    (let [data-pagamento (str (LocalDate/now))
          input {:codigo-acao "VALE3" :data-pagamento data-pagamento :tipo "DIVIDENDO" :valor 2.73 :taxa-desconto 0.15}
          output (registra-novo-provento! (partial swap! proventos-repository conj) input)]
      (is (= 1 (count @proventos-repository)))
      (is (= :clojure.spec.alpha/valid (get output :valid :clojure.spec.alpha/invalid)))))

  (testing "Não deveria gerar um novo provento por erros de validação"
    (let [data-pagamento (str (LocalDate/now))
          input {:codigo-acao "VALE3" :data-pagamento data-pagamento :tipo "DIVIDENDO"}
          output (registra-novo-provento! (partial swap! proventos-repository conj) input)]
      (is (= 1 (count @proventos-repository)))
      (is (= :clojure.spec.alpha/invalid (get output :valid))))))

(deftest total-recebido-test
  (testing "Deveria o total recebido em proventos a partir do código de uma ação válido"
    (let [provento-1 (new-provento (random-uuid) "PETR4" (str (LocalDate/of 2024 04 05)) "JSCP" 0.54)
          provento-2 (new-provento (random-uuid) "VALE3" (str (LocalDate/now)) "DIVIDENDO" 2.70)
          proventos [provento-1 provento-2]
          output (total-recebido "VALE3" proventos)]
      (is (= 2.70 (get output :total-recebido)))))
  
  (testing "Não deveria retornar o total recebido por causa do código de ação inválido"
    (let [provento-1 (new-provento (random-uuid) "PETR4" (str (LocalDate/of 2024 04 05)) "JSCP" 0.54)
          provento-2 (new-provento (random-uuid) "VALE3" (str (LocalDate/of 2024 06 05)) "DIVIDENDO" 2.70)
          proventos [provento-1 provento-2]
          output (total-recebido "VAL3" proventos)]
      (is (= :clojure.spec.alpha/invalid (get output :valid))))))
