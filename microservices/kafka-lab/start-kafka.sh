#!/bin/bash
#
# Start Kafka cluster: ZooKeeper + 3 Brokers + Kafka UI
# Usage: ./start-kafka.sh
# Stop:  ./stop-kafka.sh
#

KAFKA_LAB="$(cd "$(dirname "$0")" && pwd)"
BROKER1="$KAFKA_LAB/kafka-101"
BROKER2="$KAFKA_LAB/kafka-102"
BROKER3="$KAFKA_LAB/kafka-103"
KAFKA_UI="$KAFKA_LAB/kafka-ui"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}  Kafka Cluster Startup${NC}"
echo -e "${CYAN}========================================${NC}"

# --- 1. ZooKeeper ---
echo -e "\n${YELLOW}[1/5] Starting ZooKeeper on port 2181...${NC}"
"$BROKER1/bin/zookeeper-server-start.sh" -daemon "$BROKER1/config/zookeeper.properties"
sleep 5
echo -e "${GREEN}  ZooKeeper started.${NC}"

# --- 2. Broker 101 ---
echo -e "\n${YELLOW}[2/5] Starting Broker 101 on port 9092...${NC}"
"$BROKER1/bin/kafka-server-start.sh" -daemon "$BROKER1/config/server.properties"
sleep 3
echo -e "${GREEN}  Broker 101 started.${NC}"

# --- 3. Broker 102 ---
echo -e "\n${YELLOW}[3/5] Starting Broker 102 on port 9093...${NC}"
"$BROKER2/bin/kafka-server-start.sh" -daemon "$BROKER2/config/server.properties"
sleep 3
echo -e "${GREEN}  Broker 102 started.${NC}"

# --- 4. Broker 103 ---
echo -e "\n${YELLOW}[4/5] Starting Broker 103 on port 9094...${NC}"
"$BROKER3/bin/kafka-server-start.sh" -daemon "$BROKER3/config/server.properties"
sleep 3
echo -e "${GREEN}  Broker 103 started.${NC}"

# --- 5. Kafka UI ---
echo -e "\n${YELLOW}[5/5] Starting Kafka UI on port 8080...${NC}"
cd "$KAFKA_UI"
nohup java --add-opens java.rmi/javax.rmi.ssl=ALL-UNNAMED -Dspring.config.additional-location=application.yml -jar kafka-ui-api-v0.7.2.jar > kafka-ui.log 2>&1 &
echo $! > kafka-ui.pid
sleep 2
echo -e "${GREEN}  Kafka UI started (PID: $(cat kafka-ui.pid)).${NC}"

echo -e "\n${CYAN}========================================${NC}"
echo -e "${GREEN}  Cluster is UP!${NC}"
echo -e "${CYAN}========================================${NC}"
echo -e "  ZooKeeper  : localhost:2181"
echo -e "  Broker 101 : localhost:9092"
echo -e "  Broker 102 : localhost:9093"
echo -e "  Broker 103 : localhost:9094"
echo -e "  Kafka UI   : ${CYAN}http://localhost:8080${NC}"
echo -e "${CYAN}========================================${NC}"
