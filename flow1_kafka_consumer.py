import json
import logging
from confluent_kafka import Consumer, KafkaError

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

# 1. Kafka Consumer Configuration for Flow 1
KAFKA_CONF = {
    'bootstrap.servers': 'istad-kafka-1:9090,istad-kafka-2:9090,istad-kafka-3:9090',
    'group.id': 'databricks-python-bronze-consumer',
    'auto.offset.reset': 'earliest',
    'enable.auto.commit': False,  # Manual Offset Management for reliability
}

TOPIC = 'cdc.public.customers'

def transform_cdc_event(payload):
    """
    Extract and transform CDC event
    Handles INSERT ('c', 'r'), UPDATE ('u'), DELETE ('d') operations
    """
    op = payload.get('op')
    before = payload.get('before') or {}
    after = payload.get('after') or {}
    ts_ms = payload.get('ts_ms')

    # Coalesce ID between after and before
    record_id = after.get('id') if after.get('id') is not None else before.get('id')
    
    return {
        "id": record_id,
        "name": after.get('name'),
        "email": after.get('email'),
        "city": after.get('city'),
        "op": op,
        "ts_ms": ts_ms
    }

def ingest_to_databricks_bronze(batch):
    """
    Ingest raw CDC data into Databricks Bronze format / storage
    """
    logging.info(f"🚀 Ingesting {len(batch)} records into Databricks Bronze Table...")
    with open("bronze_customer_events.json", "a", encoding="utf-8") as f:
        for item in batch:
            f.write(json.dumps(item) + "\n")
    logging.info("✅ Batch successfully written to Bronze layer.")

def run_consumer():
    consumer = Consumer(KAFKA_CONF)
    consumer.subscribe([TOPIC])
    logging.info(f"Subscribed to topic: {TOPIC}")

    processed_keys = set()  # In-memory deduplication tracking
    batch = []
    BATCH_SIZE = 50

    try:
        while True:
            msg = consumer.poll(timeout=1.0)
            if msg is None:
                if batch:
                    ingest_to_databricks_bronze(batch)
                    consumer.commit()  # Manual offset commit
                    batch.clear()
                    processed_keys.clear()
                continue

            if msg.error():
                if msg.error().code() == KafkaError._PARTITION_EOF:
                    continue
                else:
                    logging.error(f"Kafka error: {msg.error()}")
                    break

            try:
                val_str = msg.value().decode('utf-8', errors='ignore')
                start_idx = val_str.find('{')
                if start_idx != -1:
                    raw_event = json.loads(val_str[start_idx:])
                    payload = raw_event.get('payload', raw_event)
                    event = transform_cdc_event(payload)

                    # Deduplication handling: (ID, ts_ms, op)
                    event_key = f"{event['id']}_{event['ts_ms']}_{event['op']}"
                    if event_key not in processed_keys:
                        processed_keys.add(event_key)
                        batch.append(event)
                        logging.info(f"Captured Event: ID={event['id']}, OP={event['op']}, Name={event['name']}")

                if len(batch) >= BATCH_SIZE:
                    ingest_to_databricks_bronze(batch)
                    consumer.commit()  # Manual offset commit
                    batch.clear()
                    processed_keys.clear()

            except Exception as e:
                logging.warning(f"Skipping unparseable message: {e}")

    except KeyboardInterrupt:
        logging.info("Consumer stopped by user.")
    finally:
        consumer.close()

if __name__ == '__main__':
    run_consumer()
