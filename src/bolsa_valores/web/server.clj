(ns bolsa-valores.web.server
  (:require [bolsa-valores.web.routes :refer [app-routes]]
            [cheshire.generate :as gen]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.logger :refer [wrap-log-request-params
                                 wrap-log-request-start wrap-log-response
                                 wrap-with-logger]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.reload :refer [wrap-reload]])
  (:import [java.time LocalDate]))

(extend-protocol gen/JSONable
  LocalDate
  (to-json [local-date gen]
    (gen/write-string gen (str local-date))))

(def app
  (-> app-routes
      wrap-log-response
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-json-response {:keywords? true :bigdecimals? true})
      (wrap-log-request-params {:transform-fn #(assoc % :level :info)})
      (wrap-params {:encoding "UTF-8"})
      (wrap-defaults api-defaults)
      wrap-log-request-start
      wrap-reload))

(defn run-server [port join?]
  (run-jetty (wrap-with-logger app) {:port port :join? join?}))