#!/bin/bash
#
# Download and setup 2-node Cassandra 5.0 cluster for local development
# Usage: ./setup-cassandra.sh
#
# Cassandra 5.0 has native Java 17 support — no JVM hacks needed
#
# Nodes:
#   Node 1: localhost:9042 (seed)
#   Node 2: localhost:9043
#

CASSANDRA_LAB="$(cd "$(dirname "$0")" && pwd)"
CASSANDRA_VERSION="5.0.4"
DOWNLOAD_URL="https://archive.apache.org/dist/cassandra/${CASSANDRA_VERSION}/apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz"
TARBALL="apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${CYAN}================================================${NC}"
echo -e "${CYAN}  Cassandra 2-Node Cluster Setup (v${CASSANDRA_VERSION})${NC}"
echo -e "${CYAN}================================================${NC}"

# Check Java
java -version 2>/dev/null
if [ $? -ne 0 ]; then
    echo -e "${RED}Java 17+ is required.${NC}"
    exit 1
fi

cd "$CASSANDRA_LAB"

# Download if not exists
if [ ! -f "$CASSANDRA_LAB/$TARBALL" ] && [ ! -d "$CASSANDRA_LAB/cassandra-node-1" ]; then
    echo -e "${YELLOW}Downloading Cassandra ${CASSANDRA_VERSION}...${NC}"
    curl -L -O "$DOWNLOAD_URL"
    if [ $? -ne 0 ]; then
        echo -e "${RED}Download failed.${NC}"
        exit 1
    fi
fi

# Setup 2 nodes
for NODE_NUM in 1 2; do
    NODE_DIR="$CASSANDRA_LAB/cassandra-node-${NODE_NUM}"

    if [ -d "$NODE_DIR" ]; then
        echo -e "${GREEN}Node ${NODE_NUM} already exists.${NC}"
        continue
    fi

    echo -e "${YELLOW}Setting up Node ${NODE_NUM}...${NC}"
    tar -xzf "$TARBALL" -C "$CASSANDRA_LAB"
    mv "$CASSANDRA_LAB/apache-cassandra-${CASSANDRA_VERSION}" "$NODE_DIR"

    mkdir -p "$NODE_DIR/data/data" "$NODE_DIR/data/commitlog" "$NODE_DIR/data/saved_caches" "$NODE_DIR/data/hints" "$NODE_DIR/logs"

    YAML="$NODE_DIR/conf/cassandra.yaml"
    ENV_SH="$NODE_DIR/conf/cassandra-env.sh"

    # Patch data directories
    sed -i '' "s|/var/lib/cassandra/data|$NODE_DIR/data/data|g" "$YAML"
    sed -i '' "s|/var/lib/cassandra/commitlog|$NODE_DIR/data/commitlog|g" "$YAML"
    sed -i '' "s|/var/lib/cassandra/saved_caches|$NODE_DIR/data/saved_caches|g" "$YAML"
    sed -i '' "s|/var/lib/cassandra/hints|$NODE_DIR/data/hints|g" "$YAML"

    # Cluster name
    sed -i '' "s|cluster_name: 'Test Cluster'|cluster_name: 'npci-local-cluster'|g" "$YAML"

    case $NODE_NUM in
        1) NATIVE_PORT=9042; STORAGE_PORT=7000; JMX_PORT=7199 ;;
        2) NATIVE_PORT=9043; STORAGE_PORT=7001; JMX_PORT=7299 ;;
    esac

    # Patch ports
    sed -i '' "s|native_transport_port: 9042|native_transport_port: ${NATIVE_PORT}|g" "$YAML"
    sed -i '' "s|storage_port: 7000|storage_port: ${STORAGE_PORT}|g" "$YAML"

    # Patch JMX port
    sed -i '' "s|JMX_PORT=\"7199\"|JMX_PORT=\"${JMX_PORT}\"|g" "$ENV_SH"

    # Reduce heap for local dev (256M per node)
    sed -i '' '2i\
# Local dev reduced heap\
MAX_HEAP_SIZE="256M"\
HEAP_NEWSIZE="128M"\
' "$ENV_SH"

    echo -e "${GREEN}  Node ${NODE_NUM}: native=${NATIVE_PORT}, storage=${STORAGE_PORT}, jmx=${JMX_PORT}, heap=256M${NC}"
done

rm -f "$CASSANDRA_LAB/$TARBALL"

echo -e "\n${CYAN}================================================${NC}"
echo -e "${GREEN}  Setup Complete! (Cassandra ${CASSANDRA_VERSION})${NC}"
echo -e "${CYAN}================================================${NC}"
echo -e "  Node 1 (seed) : localhost:${CYAN}9042${NC}"
echo -e "  Node 2        : localhost:${CYAN}9043${NC}"
echo -e ""
echo -e "  Start : ${CYAN}./start-cassandra.sh${NC}"
echo -e "  Stop  : ${CYAN}./stop-cassandra.sh${NC}"
echo -e "  CQL   : ${CYAN}./cassandra-node-1/bin/cqlsh localhost 9042${NC}"
echo -e "  Schema: ${CYAN}./cassandra-node-1/bin/cqlsh -f init-schema.cql${NC}"
echo -e "${CYAN}================================================${NC}"
