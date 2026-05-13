#!/bin/bash
#
# Download and setup 3-node Cassandra cluster for local development
# Usage: ./setup-cassandra.sh
#
# Teaches: Replication Factor, Consistency Levels
#   Node 1: localhost:9042 (seed)
#   Node 2: localhost:9043
#   Node 3: localhost:9044
#

CASSANDRA_LAB="$(cd "$(dirname "$0")" && pwd)"
CASSANDRA_VERSION="4.1.8"
DOWNLOAD_URL="https://archive.apache.org/dist/cassandra/${CASSANDRA_VERSION}/apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz"
TARBALL="apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}================================================${NC}"
echo -e "${CYAN}  Cassandra 3-Node Cluster Setup (v${CASSANDRA_VERSION})${NC}"
echo -e "${CYAN}================================================${NC}"

# Check Java
java -version 2>/dev/null
if [ $? -ne 0 ]; then
    echo -e "${YELLOW}Java is required. Please install Java 17+.${NC}"
    exit 1
fi

# Download if not exists
if [ ! -f "$CASSANDRA_LAB/$TARBALL" ] && [ ! -d "$CASSANDRA_LAB/cassandra-node-1" ]; then
    echo -e "${YELLOW}Downloading Cassandra ${CASSANDRA_VERSION}...${NC}"
    cd "$CASSANDRA_LAB"
    curl -L -O "$DOWNLOAD_URL"
fi

# Setup 3 nodes
for NODE_NUM in 1 2 3; do
    NODE_DIR="$CASSANDRA_LAB/cassandra-node-${NODE_NUM}"

    if [ -d "$NODE_DIR" ]; then
        echo -e "${GREEN}Node ${NODE_NUM} already exists.${NC}"
        continue
    fi

    echo -e "${YELLOW}Setting up Node ${NODE_NUM}...${NC}"

    # Extract fresh copy
    tar -xzf "$TARBALL" -C "$CASSANDRA_LAB"
    mv "$CASSANDRA_LAB/apache-cassandra-${CASSANDRA_VERSION}" "$NODE_DIR"

    # Create data dirs
    mkdir -p "$NODE_DIR/data/data" "$NODE_DIR/data/commitlog" "$NODE_DIR/data/saved_caches" "$NODE_DIR/data/hints" "$NODE_DIR/logs"

    YAML="$NODE_DIR/conf/cassandra.yaml"

    # Patch data directories
    sed -i '' "s|/var/lib/cassandra/data|$NODE_DIR/data/data|g" "$YAML"
    sed -i '' "s|/var/lib/cassandra/commitlog|$NODE_DIR/data/commitlog|g" "$YAML"
    sed -i '' "s|/var/lib/cassandra/saved_caches|$NODE_DIR/data/saved_caches|g" "$YAML"
    sed -i '' "s|/var/lib/cassandra/hints|$NODE_DIR/data/hints|g" "$YAML"

    # Patch cluster name
    sed -i '' "s|cluster_name: 'Test Cluster'|cluster_name: 'npci-local-cluster'|g" "$YAML"

    # Patch seeds (node-1 is the seed)
    sed -i '' "s|- seeds: \"127.0.0.1:7000\"|- seeds: \"127.0.0.1:7000\"|g" "$YAML"

    # Each node needs unique ports and listen address
    # We use 127.0.0.1 for all but different native_transport_port and storage_port
    case $NODE_NUM in
        1)
            NATIVE_PORT=9042
            STORAGE_PORT=7000
            JMX_PORT=7199
            ;;
        2)
            NATIVE_PORT=9043
            STORAGE_PORT=7001
            JMX_PORT=7299
            ;;
        3)
            NATIVE_PORT=9044
            STORAGE_PORT=7002
            JMX_PORT=7399
            ;;
    esac

    # Patch ports
    sed -i '' "s|native_transport_port: 9042|native_transport_port: ${NATIVE_PORT}|g" "$YAML"
    sed -i '' "s|storage_port: 7000|storage_port: ${STORAGE_PORT}|g" "$YAML"

    # Patch JMX port in cassandra-env.sh
    ENV_SH="$NODE_DIR/conf/cassandra-env.sh"
    sed -i '' "s|JMX_PORT=\"7199\"|JMX_PORT=\"${JMX_PORT}\"|g" "$ENV_SH"

    # Patch log directory
    LOG4J="$NODE_DIR/conf/logback.xml"
    sed -i '' "s|\${cassandra.logdir}/|$NODE_DIR/logs/|g" "$LOG4J" 2>/dev/null

    echo -e "${GREEN}  Node ${NODE_NUM}: native=${NATIVE_PORT}, storage=${STORAGE_PORT}, jmx=${JMX_PORT}${NC}"
done

# Cleanup tarball
rm -f "$CASSANDRA_LAB/$TARBALL"

echo -e "\n${CYAN}================================================${NC}"
echo -e "${GREEN}  3-Node Cluster Setup Complete!${NC}"
echo -e "${CYAN}================================================${NC}"
echo -e "  Node 1 : localhost:${CYAN}9042${NC} (seed)"
echo -e "  Node 2 : localhost:${CYAN}9043${NC}"
echo -e "  Node 3 : localhost:${CYAN}9044${NC}"
echo -e ""
echo -e "  Start : ${CYAN}./start-cassandra.sh${NC}"
echo -e "  Stop  : ${CYAN}./stop-cassandra.sh${NC}"
echo -e "  CQL   : ${CYAN}./cassandra-node-1/bin/cqlsh localhost 9042${NC}"
echo -e "  Schema: ${CYAN}./cassandra-node-1/bin/cqlsh -f init-schema.cql${NC}"
echo -e "${CYAN}================================================${NC}"
