FROM clojure:temurin-21-lein-2.11.2-alpine AS build
COPY . /app/
WORKDIR /app/
RUN lein clean && lein deps
RUN lein with-profile docker uberjar

FROM eclipse-temurin:21.0.4_7-jre-alpine AS final
COPY --from=build /app/target/uberjar/bolsa-valores-0.1.0-SNAPSHOT-standalone.jar /app/bolsa-valores.jar
WORKDIR /app/
ENTRYPOINT java -Dconf="config/docker.edn" -jar ./bolsa-valores.jar