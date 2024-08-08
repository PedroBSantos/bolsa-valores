(ns bolsa-valores.web.routes
  (:require [bolsa-valores.web.handlers :as h]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]))

(defroutes app-routes
  (POST "/proventos/adicionar-provento" [] h/novo-provento-handler)
  (GET "/proventos" [] h/proventos-handler)
  (POST "/compras/adicionar-compra" [] h/nova-compra-handler)
  (GET "/compras" [] h/compras-handler)
  (GET "/compras/descrever*" [] h/descrever-compras-handler)
  (GET "/compras/total-comprado-na-acao*" [] h/total-comprado-na-acao-handler)
  (GET "/compras/total-comprado" [] h/total-comprado-handler)
  (GET "/proventos/total-recebido-na-acao*" [] h/total-recebido-na-acao-handler)
  (GET "/proventos/total-recebido" [] h/total-recebido-handler)
  (route/not-found "Path Not Found"))