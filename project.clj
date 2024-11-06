(defproject bolsa-valores "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [com.layerware/hugsql "0.5.3"]
                 [com.layerware/hugsql-adapter-next-jdbc "0.5.3"]
                 [org.postgresql/postgresql "42.7.3"]
                 [ring/ring-core "1.12.1"]
                 [ring/ring-jetty-adapter "1.12.1"]
                 [ring/ring-devel "1.12.1"]
                 [ring/ring-json "0.5.1"]
                 [compojure/compojure "1.7.1"]
                 [org.clojure/tools.logging "1.3.0"]
                 [ring/ring-defaults "0.4.0"]
                 [ring-logger/ring-logger "1.1.1"]
                 [org.clojure/data.json "2.5.0"]
                 [clj-test-containers "0.7.4"]
                 [ring-cors "0.1.13"]
                 [com.github.steffan-westcott/clj-otel-api "0.2.7"]
                 [org.apache.logging.log4j/log4j-core "2.24.1"]
                 [org.apache.logging.log4j/log4j-jcl "2.24.1"]
                 [org.apache.logging.log4j/log4j-jul "2.24.1"]
                 [org.apache.logging.log4j/log4j-slf4j2-impl "2.24.1"]]
  :main ^:skip-aot bolsa-valores.core
  :target-path "target/%s"
  :profiles {:dev {:aot :all
                   :jvm-opts ["-javaagent:resources/otel/opentelemetry-javaagent.jar"
                              "-Dotel.service.name=bolsa-valores"
                              "-Dotel.exporter.otlp.endpoint=http://localhost:4317"
                              "-Dotel.instrumentation.http.client.enabled=true"
                              "-Dotel.traces.exporter=otlp"
                              "-Dotel.metrics.exporter=otlp" 
                              "-Dotel.logs.exporter=otlp"
                              "-Dotel.exporter.otlp.protocol=grpc"
                              "-Dclojure.compiler.direct-linking=true" 
                              "-Dconf=config/dev.edn"
                              "-Dclojure.tools.logging.factory=clojure.tools.logging.impl/log4j2-factory"
                              "-Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager"]}
             :docker {:aot :all
                      :jvm-opts ["-Dclojure.compiler.direct-linking=true" "-Dconf=config/docker.edn"]}})
