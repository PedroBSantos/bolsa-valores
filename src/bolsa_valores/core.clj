(ns bolsa-valores.core
  (:gen-class)
  (:require [bolsa-valores.web.server :refer [run-server]]))

(defn -main
  [& _]
  (run-server 8080 false))
