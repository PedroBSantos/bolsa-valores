FROM clojure:temurin-23-lein-2.11.2-alpine AS build
COPY . /app/
WORKDIR /app/
RUN lein clean && lein deps
RUN lein with-profile docker uberjar

FROM eclipse-temurin:23.0.1_11-jre-alpine AS final
COPY --from=build /app/target/uberjar/bolsa-valores-0.1.0-SNAPSHOT-standalone.jar /app/bolsa-valores.jar
WORKDIR /app/
RUN wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.11.0/opentelemetry-javaagent.jar
ENTRYPOINT java -javaagent:./opentelemetry-javaagent.jar \
    -Dotel.service.name=bolsa-valores \
    -Dotel.exporter.otlp.endpoint=http://otlp-container:4317 \
    -Dotel.instrumentation.http.client.enabled=true \
    -Dotel.traces.exporter=otlp \
    -Dotel.metrics.exporter=otlp \
    -Dotel.logs.exporter=otlp \
    -Dotel.exporter.otlp.protocol=grpc \
    -Dconf="config/docker.edn" \
    -Dclojure.tools.logging.factory=clojure.tools.logging.impl/log4j2-factory \
    -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager \
    -jar ./bolsa-valores.jar