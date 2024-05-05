(ns bolsa-valores.database.database-config
  (:require [hugsql.core :as hugsql]
            [hugsql.adapter.next-jdbc :as next-adapter]
            [clojure.edn :as edn]))

(def config (edn/read-string (slurp "config.edn")))
(def database-config (:database config))

(hugsql/def-db-fns
  "bolsa_valores/database/sql/queries.sql"
  {:adapter (next-adapter/hugsql-adapter-next-jdbc)})

(hugsql/def-sqlvec-fns
  "bolsa_valores/database/sql/queries.sql"
  {:adapter (next-adapter/hugsql-adapter-next-jdbc)})