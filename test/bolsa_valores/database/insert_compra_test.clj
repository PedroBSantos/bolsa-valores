(ns bolsa-valores.database.insert-compra-test
  (:require [bolsa-valores.core.api.compras :refer [registra-nova-compra!]]
            [bolsa-valores.core.domain.model :refer [new-compra]]
            [bolsa-valores.database.queries :refer [insert-compra
                                                    read-all-compras]]
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

(deftest insert-compra-test
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

    (testing "Deveria inserir uma compra no banco de dados"
      (let [id (random-uuid)
            compra (new-compra id "VALE3" (str (LocalDate/now)) 60.0 1)
            compras-before (read-all-compras database-config)
            _ (insert-compra database-config compra)
            compras-after (read-all-compras database-config)]
        (is (> (count compras-after) (count compras-before)))))

    (testing "Deveria inserir uma compra no banco de dados passando pela camada de api"
      (let [data-compra (str (LocalDate/now))
            input {:codigo-acao "VALE3" :data-compra data-compra :valor-acao 60.0 :quantidade 1}
            compras-before (read-all-compras database-config)
            output (registra-nova-compra! (partial insert-compra database-config) input)
            compras-after (read-all-compras database-config)]
        (is (= :clojure.spec.alpha/valid (get output :valid :clojure.spec.alpha/invalid)))
        (is (> (count compras-after) (count compras-before)))))

    (tc/stop! container)))