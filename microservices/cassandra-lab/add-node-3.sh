#!/bin/bash
#
# Add a 3rd Cassandra node to the cluster
# Usage: ./add-node-3.sh
#
# Run this when you have ~256M free memory
# After adding, update keyspace to RF=3:
#   ALTER KEYSPACE mts WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};
#   nodetool -p 7199 repair mts
#

CASSANDRA_LAB="$(cd "$(dirname "$0")" && pwd)"
NODE_DIR="$CASSANDRA_LAB/cassandra-node-3"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

# Setup node 3 if not exists
if [ ! -d "$NODE_DIR" ]; then
    CASSANDRA_VERSION="5.0.4"
    TARBALL="apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz"

    if [ ! -f "$CASSANDRA_LAB/$TARBALL" ]; then
        echo -e "${YELLOW}Downloading Cassandra ${CASSANDRA_VERSION}...${NC}"
        cd "$CASSANDRA_LAB"
        curl -L -O "https://archive.apache.org/dist/cassandra/${CASSANDRA_VERSION}/apache-cassandra-${CASSANDRA_VERSION}-bin.tar.gz"
    fi

    echo -e "${YELLOW}Setting up Node 3...${NC}"
    tar -xzf "$TARBALL" -C "$CASSANDRA_LAB"
    mv "$CASSANDRA_LAB/apache-cassandra-${CASSANDRA_VERSION}" "$NODE_DIR"
    mkdir -p "$NODE_DIR/data/data" "$NODE_DIR/data/commitlog" "$NODE_DIR/data/saved_caches" "$NODE_DIR/data/hints" "$NODE_DIR/logs"

    YAML="$NODE_DIR/conf/cassandra.yaml"
    ENV_SH="$NODE_DIR/conf/cassandra-env.sh"

    sed -i '' "s|/var/lib/cassandra/data|$NODE_DIR/data/data|g" "$YAML"
    sed -i '' "s|/var/lib/cassandra/commitlog|$NODE_DIR/data/commitlog|g" "$YAML"
    sed -i '' "s|/var/lib/cassandra/saved_caches|$NODE_DIR/data/saved_caches|g" "$YAML"
    sed -i '' "s|/var/lib/cassandra/hints|$NODE_DIR/data/hints|g" "$YAML"
    sed -i '' "s|cluster_name: 'Test Cluster'|cluster_name: 'npci-local-cluster'|g" "$YAML"
    sed -i '' "s|native_transport_port: 9042|native_transport_port: 9044|g" "$YAML"
    sed -i '' "s|storage_port: 7000|storage_port: 7002|g" "$YAML"
    sed -i '' "s|JMX_PORT=\"7199\"|JMX_PORT=\"7399\"|g" "$ENV_SH"

    # Reduce heap
    sed -i '' '2i\
# Local dev reduced heap\
MAX_HEAP_SIZE="256M"\
HEAP_NEWSIZE="128M"\
' "$ENV_SH"

    rm -f "$CASSANDRA_LAB/$TARBALL"
    echo -e "${GREEN}Node 3 setup complete.${NC}"
fi

# Start node 3
echo -e "${YELLOW}Starting Node 3 on port 9044...${NC}"
CASSANDRA_LOG_DIR="$NODE_DIR/logs" "$NODE_DIR/bin/cassandra" -R -p "$NODE_DIR/cassandra.pid" > /dev/null 2>&1

echo -e "${YELLOW}Waiting for Node 3 to join the ring...${NC}"
sleep 30

echo -e "${GREEN}Node 3 started!${NC}"
"$CASSANDRA_LAB/cassandra-node-1/bin/nodetool" -p 7199 status

echo -e "\n${CYAN}Next steps:${NC}"
echo -e "  1. Upgrade RF to 3:"
echo -e "     ${GREEN}ALTER KEYSPACE mts WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};${NC}"
echo -e "  2. Repair to replicate data to new node:"
echo -e "     ${GREEN}./cassandra-node-1/bin/nodetool -p 7199 repair mts${NC}"
echo -e "  3. Now CL demos work with 3 nodes (stop 1, QUORUM still works)"
