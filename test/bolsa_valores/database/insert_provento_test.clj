(ns bolsa-valores.database.insert-provento-test
  (:require [bolsa-valores.core.api.proventos :refer [registra-novo-provento!]]
            [bolsa-valores.core.domain.model :refer [new-provento]]
            [bolsa-valores.database.queries :refer [insert-provento
                                                    read-all-proventos]]
            [clj-test-containers.core :as tc]
            [clojure.test :refer [deftest is testing]])
  (:import [java.time LocalDate]))

(defn create-postgres-container [config]
  (-> (tc/create-from-docker-file {:docker-file (:docker-file config)
                                   :exposed-ports [(:port config)]
                                   :env-vars (:env config)
                                   :network (:network config)
                                   :network-aliases [(:network-aliases config)]
                                   :wait-for {:wait-strategy :log
                                              :message "accept connections"
                                              :startup-timeout 10}})
      (tc/start!)))

(deftest insert-provento-test
  (let [container (create-postgres-container {:docker-file "test/bolsa_valores/database/Dockerfile.TestContainers"
                                              :port 5432
                                              :env {}
                                              :network "dev"})
        mapped-port (str (get (:mapped-ports container) 5432))
        database-config {:subname (format "//localhost:%s/postgres" mapped-port)
                         :host "localhost"
                         :port mapped-port
                         :dbname "bolsa_valores"
                         :subprotocol "postgres"
                         :dbtype "postgres"
                         :user "postgres"
                         :password "postgres"}
        _ (Thread/sleep 2000)]

    (testing "Deveria inserir um provento no banco de dados"
      (let [id (random-uuid)
            provento (new-provento id "PETR4" (str (LocalDate/of 2024 04 05)) "JSCP" 0.54)
            proventos-before (read-all-proventos database-config)
            _ (insert-provento database-config provento)
            proventos-after (read-all-proventos database-config)]
        (is (> (count proventos-after) (count proventos-before)))))

    (testing "Deveria inserir um provento no banco de dados passando pela camada de api"
      (let [data-pagamento (str (LocalDate/now))
            input {:codigo-acao "VALE3" :data-pagamento data-pagamento :tipo "JSCP" :valor 2.73 :taxa-desconto 0.15}
            proventos-before (read-all-proventos database-config)
            output (registra-novo-provento! (partial insert-provento database-config) input)
            proventos-after (read-all-proventos database-config)]
        (is (= :clojure.spec.alpha/valid (get output :valid :clojure.spec.alpha/invalid)))
        (is (> (count proventos-after) (count proventos-before)))))

    (tc/stop! container)))