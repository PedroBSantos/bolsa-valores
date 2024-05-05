(ns bolsa-valores.database.queries
  #_{:clj-kondo/ignore [:unresolved-var]}
  (:require [bolsa-valores.database.database-config :refer [database-config
                                                            sql-insert-compra
                                                            sql-insert-provento
                                                            sql-read-all-compras
                                                            sql-read-all-proventos]]))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn insert-compra
  ([compra] (sql-insert-compra database-config compra))
  ([db-config compra] (sql-insert-compra db-config compra)))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn insert-provento
  ([provento] (sql-insert-provento database-config provento))
  ([db-config provento] (sql-insert-provento db-config provento)))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn read-all-compras
  ([] (sql-read-all-compras database-config {}))
  ([db-config] (sql-read-all-compras db-config {})))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn read-all-proventos
  ([] (sql-read-all-proventos database-config {}))
  ([db-config] (sql-read-all-proventos db-config {})))