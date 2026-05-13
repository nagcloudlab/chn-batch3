#!/bin/bash
#
# Stop Kafka cluster: Kafka UI + 3 Brokers + ZooKeeper
#

KAFKA_LAB="$(cd "$(dirname "$0")" && pwd)"
BROKER1="$KAFKA_LAB/kafka-101"
BROKER2="$KAFKA_LAB/kafka-102"
BROKER3="$KAFKA_LAB/kafka-103"
KAFKA_UI="$KAFKA_LAB/kafka-ui"

RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}Stopping Kafka cluster...${NC}"

# Kafka UI
if [ -f "$KAFKA_UI/kafka-ui.pid" ]; then
    kill "$(cat "$KAFKA_UI/kafka-ui.pid")" 2>/dev/null
    rm -f "$KAFKA_UI/kafka-ui.pid"
    echo -e "${RED}  Kafka UI stopped.${NC}"
fi

# Brokers
"$BROKER3/bin/kafka-server-stop.sh" 2>/dev/null; echo -e "${RED}  Broker 103 stopped.${NC}"
"$BROKER2/bin/kafka-server-stop.sh" 2>/dev/null; echo -e "${RED}  Broker 102 stopped.${NC}"
"$BROKER1/bin/kafka-server-stop.sh" 2>/dev/null; echo -e "${RED}  Broker 101 stopped.${NC}"

sleep 3

# ZooKeeper
"$BROKER1/bin/zookeeper-server-stop.sh" 2>/dev/null; echo -e "${RED}  ZooKeeper stopped.${NC}"

echo -e "\n${YELLOW}Cluster is DOWN.${NC}"
