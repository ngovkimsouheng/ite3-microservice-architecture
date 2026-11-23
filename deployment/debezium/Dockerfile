ARG DEBEZIUM_VERSION=2.6.2.Final
ARG ICEBERG_SINK_VERSION=0.6.19

# Stage 1: Download Debezium Plugins & Iceberg Sink Connector
FROM alpine:3.20 AS downloader
ARG DEBEZIUM_VERSION
ARG ICEBERG_SINK_VERSION

RUN apk add --no-cache curl tar gzip unzip

RUN mkdir -p /out/debezium-postgres /out/debezium-jdbc /out/iceberg-kafka-connect \
  && curl -fSL --retry 3 \
       "https://repo.maven.apache.org/maven2/io/debezium/debezium-connector-postgres/${DEBEZIUM_VERSION}/debezium-connector-postgres-${DEBEZIUM_VERSION}-plugin.tar.gz" \
     | tar -xz --strip-components=1 -C /out/debezium-postgres \
  && curl -fSL --retry 3 \
       "https://repo.maven.apache.org/maven2/io/debezium/debezium-connector-jdbc/${DEBEZIUM_VERSION}/debezium-connector-jdbc-${DEBEZIUM_VERSION}-plugin.tar.gz" \
     | tar -xz --strip-components=1 -C /out/debezium-jdbc \
  && curl -fSL --retry 3 \
       "https://github.com/databricks/iceberg-kafka-connect/releases/download/v${ICEBERG_SINK_VERSION}/iceberg-kafka-connect-runtime-${ICEBERG_SINK_VERSION}.zip" \
       -o /tmp/iceberg.zip \
  && unzip -q /tmp/iceberg.zip -d /out/iceberg-kafka-connect \
  && rm -f /tmp/iceberg.zip

# Stage 2: Confluent Kafka Connect 7.7.1
FROM confluentinc/cp-kafka-connect:7.7.1

COPY --from=downloader /out/debezium-postgres /usr/share/confluent-hub-components/debezium-connector-postgres
COPY --from=downloader /out/debezium-jdbc /usr/share/confluent-hub-components/debezium-connector-jdbc
COPY --from=downloader /out/iceberg-kafka-connect /usr/share/confluent-hub-components/iceberg-kafka-connect
