ARG DEBEZIUM_VERSION=2.6.2.Final

# Stage 1: Download Debezium Plugins
FROM alpine:3.20 AS downloader
ARG DEBEZIUM_VERSION

RUN apk add --no-cache curl tar gzip

RUN mkdir -p /out/debezium-postgres /out/debezium-jdbc \
  && curl -fSL --retry 3 \
       "https://repo.maven.apache.org/maven2/io/debezium/debezium-connector-postgres/${DEBEZIUM_VERSION}/debezium-connector-postgres-${DEBEZIUM_VERSION}-plugin.tar.gz" \
     | tar -xz --strip-components=1 -C /out/debezium-postgres \
  && curl -fSL --retry 3 \
       "https://repo.maven.apache.org/maven2/io/debezium/debezium-connector-jdbc/${DEBEZIUM_VERSION}/debezium-connector-jdbc-${DEBEZIUM_VERSION}-plugin.tar.gz" \
     | tar -xz --strip-components=1 -C /out/debezium-jdbc

# Stage 2: Confluent Kafka Connect 7.7.1 (Kafka 3.7.1 - compatible with Debezium JDBC)
FROM confluentinc/cp-kafka-connect:7.7.1

COPY --from=downloader /out/debezium-postgres /usr/share/confluent-hub-components/debezium-connector-postgres
COPY --from=downloader /out/debezium-jdbc /usr/share/confluent-hub-components/debezium-connector-jdbc
