#!/bin/bash
#
# Clean Kafka data (ZooKeeper + broker logs)
# Run this if brokers show "Node disconnected" in Kafka UI
# Then restart with ./start-kafka.sh
#

echo "Cleaning Kafka data..."
rm -rf /tmp/zookeeper /tmp/kafka-logs-101 /tmp/kafka-logs-102 /tmp/kafka-logs-103
echo "Done! Now run: ./start-kafka.sh"
