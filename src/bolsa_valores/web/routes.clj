(ns bolsa-valores.web.routes
  (:require [bolsa-valores.web.handlers :as h]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]))

(defroutes app-routes
  (POST "/proventos" [] h/novo-provento-handler)
  (POST "/compras" [] h/nova-compra-handler)
  (GET "/compras/total-comprado*" [] h/total-comprado-handler)
  (GET "/proventos/total-recebido*" [] h/total-recebido-handler)
  (route/not-found "Path Not Found"))