#!/bin/bash
#
# Download and setup 3-broker Kafka cluster + Kafka UI for local development
# Usage: ./setup-kafka.sh
#
# Prerequisites: Java 17+ must be installed
#
# This will create:
#   kafka-101/ (broker.id=101, port 9092)
#   kafka-102/ (broker.id=102, port 9093)
#   kafka-103/ (broker.id=103, port 9094)
#   kafka-ui/  (Kafka UI on port 8080)
#

KAFKA_LAB="$(cd "$(dirname "$0")" && pwd)"
KAFKA_VERSION="3.9.2"
SCALA_VERSION="2.13"
KAFKA_DOWNLOAD="https://archive.apache.org/dist/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz"
KAFKA_TARBALL="kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz"

KAFKA_UI_VERSION="0.7.2"
KAFKA_UI_DOWNLOAD="https://github.com/provectus/kafka-ui/releases/download/v${KAFKA_UI_VERSION}/kafka-ui-api-v${KAFKA_UI_VERSION}.jar"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${CYAN}================================================${NC}"
echo -e "${CYAN}  Kafka Cluster Setup${NC}"
echo -e "${CYAN}  Kafka ${KAFKA_VERSION} + Kafka UI ${KAFKA_UI_VERSION}${NC}"
echo -e "${CYAN}================================================${NC}"

# Check Java
java -version 2>/dev/null
if [ $? -ne 0 ]; then
    echo -e "${RED}Java is required. Please install Java 17+.${NC}"
    exit 1
fi

cd "$KAFKA_LAB"

# ============================================================
# STEP 1: Download Kafka
# ============================================================
if [ -d "$KAFKA_LAB/kafka-101" ] && [ -d "$KAFKA_LAB/kafka-102" ] && [ -d "$KAFKA_LAB/kafka-103" ]; then
    echo -e "${GREEN}All 3 broker directories already exist. Skipping Kafka download.${NC}"
else
    if [ ! -f "$KAFKA_TARBALL" ]; then
        echo -e "\n${YELLOW}[1/4] Downloading Kafka ${KAFKA_VERSION}...${NC}"
        curl -L -O "$KAFKA_DOWNLOAD"
        if [ $? -ne 0 ]; then
            echo -e "${RED}Download failed. Check your internet connection.${NC}"
            exit 1
        fi
    else
        echo -e "${GREEN}Kafka tarball already downloaded.${NC}"
    fi

    # Extract 3 copies
    for BROKER_ID in 101 102 103; do
        BROKER_DIR="$KAFKA_LAB/kafka-${BROKER_ID}"
        if [ -d "$BROKER_DIR" ]; then
            echo -e "${GREEN}  kafka-${BROKER_ID}/ already exists.${NC}"
            continue
        fi

        echo -e "${YELLOW}  Extracting kafka-${BROKER_ID}/...${NC}"
        tar -xzf "$KAFKA_TARBALL" -C "$KAFKA_LAB"
        mv "$KAFKA_LAB/kafka_${SCALA_VERSION}-${KAFKA_VERSION}" "$BROKER_DIR"

        # Configure server.properties
        CONF="$BROKER_DIR/config/server.properties"

        case $BROKER_ID in
            101) PORT=9092 ;;
            102) PORT=9093 ;;
            103) PORT=9094 ;;
        esac

        sed -i '' "s|broker.id=0|broker.id=${BROKER_ID}|g" "$CONF"
        sed -i '' "s|#listeners=PLAINTEXT://:9092|listeners=PLAINTEXT://:${PORT}|g" "$CONF"
        sed -i '' "s|#advertised.listeners=PLAINTEXT://your.host.name:9092|advertised.listeners=PLAINTEXT://localhost:${PORT}|g" "$CONF"
        sed -i '' "s|log.dirs=/tmp/kafka-logs|log.dirs=/tmp/kafka-logs-${BROKER_ID}|g" "$CONF"

        echo -e "${GREEN}  kafka-${BROKER_ID}: broker.id=${BROKER_ID}, port=${PORT}, logs=/tmp/kafka-logs-${BROKER_ID}${NC}"
    done

    # Cleanup tarball
    rm -f "$KAFKA_TARBALL"
    echo -e "${GREEN}Kafka extraction complete.${NC}"
fi

# ============================================================
# STEP 2: Download Kafka UI
# ============================================================
KAFKA_UI_DIR="$KAFKA_LAB/kafka-ui"
KAFKA_UI_JAR="$KAFKA_UI_DIR/kafka-ui-api-v${KAFKA_UI_VERSION}.jar"

mkdir -p "$KAFKA_UI_DIR"

if [ -f "$KAFKA_UI_JAR" ]; then
    echo -e "\n${GREEN}Kafka UI jar already exists.${NC}"
else
    echo -e "\n${YELLOW}[2/4] Downloading Kafka UI ${KAFKA_UI_VERSION}...${NC}"
    curl -L -o "$KAFKA_UI_JAR" "$KAFKA_UI_DOWNLOAD"
    if [ $? -ne 0 ]; then
        echo -e "${RED}Kafka UI download failed.${NC}"
        exit 1
    fi
    echo -e "${GREEN}Kafka UI downloaded.${NC}"
fi

# ============================================================
# STEP 3: Create Kafka UI config (if not exists)
# ============================================================
KAFKA_UI_YML="$KAFKA_UI_DIR/application.yml"
if [ ! -f "$KAFKA_UI_YML" ]; then
    echo -e "\n${YELLOW}[3/4] Creating Kafka UI config...${NC}"
    cat > "$KAFKA_UI_YML" <<'YAML'
kafka:
  clusters:
    - name: npci-local-cluster
      bootstrapServers: localhost:9092,localhost:9093,localhost:9094
dynamic.config.enabled: true
YAML
    echo -e "${GREEN}Kafka UI config created.${NC}"
else
    echo -e "${GREEN}Kafka UI config already exists.${NC}"
fi

# ============================================================
# STEP 4: Summary
# ============================================================
echo -e "\n${CYAN}================================================${NC}"
echo -e "${GREEN}  Setup Complete!${NC}"
echo -e "${CYAN}================================================${NC}"
echo -e ""
echo -e "  ${CYAN}Brokers:${NC}"
echo -e "    kafka-101 : broker.id=101, port ${CYAN}9092${NC}"
echo -e "    kafka-102 : broker.id=102, port ${CYAN}9093${NC}"
echo -e "    kafka-103 : broker.id=103, port ${CYAN}9094${NC}"
echo -e ""
echo -e "  ${CYAN}Kafka UI:${NC}"
echo -e "    kafka-ui/ : port ${CYAN}8080${NC}"
echo -e ""
echo -e "  ${CYAN}Commands:${NC}"
echo -e "    Start cluster : ${GREEN}./start-kafka.sh${NC}"
echo -e "    Stop cluster  : ${GREEN}./stop-kafka.sh${NC}"
echo -e ""
echo -e "  ${CYAN}Useful Kafka CLI:${NC}"
echo -e "    Create topic  : ${GREEN}./kafka-101/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic my-topic --partitions 3 --replication-factor 3${NC}"
echo -e "    List topics   : ${GREEN}./kafka-101/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list${NC}"
echo -e "    Console prod  : ${GREEN}./kafka-101/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic my-topic${NC}"
echo -e "    Console cons  : ${GREEN}./kafka-101/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic my-topic --from-beginning${NC}"
echo -e "${CYAN}================================================${NC}"
