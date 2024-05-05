(defproject bolsa-valores "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.3"]
                 [com.layerware/hugsql "0.5.3"]
                 [com.layerware/hugsql-adapter-next-jdbc "0.5.3"]
                 [org.postgresql/postgresql "42.7.3"]
                 [ring/ring-core "1.12.1"]
                 [ring/ring-jetty-adapter "1.12.1"]
                 [ring/ring-devel "1.12.1"]
                 [ring/ring-json "0.5.1"]
                 [compojure/compojure "1.7.1"]
                 [org.clojure/tools.logging "1.3.0"]
                 [org.apache.logging.log4j/log4j-core "2.23.1"]
                 [ring/ring-defaults "0.4.0"]
                 [ring-logger/ring-logger "1.1.1"]
                 [org.clojure/data.json "2.5.0"]
                 [org.slf4j/slf4j-simple "2.0.12"]
                 [clj-test-containers "0.7.4"]]
  :main ^:skip-aot bolsa-valores.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
