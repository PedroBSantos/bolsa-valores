(ns bolsa-valores.database.database-config
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [hugsql.adapter.next-jdbc :as next-adapter]
            [hugsql.core :as hugsql]))

(def application-conf (System/getProperty "conf"))
(defonce database-config (-> application-conf
                             io/resource
                             slurp
                             edn/read-string
                             :database))

(hugsql/def-db-fns
  "bolsa_valores/database/sql/queries.sql"
  {:adapter (next-adapter/hugsql-adapter-next-jdbc)})

(hugsql/def-sqlvec-fns
  "bolsa_valores/database/sql/queries.sql"
  {:adapter (next-adapter/hugsql-adapter-next-jdbc)})