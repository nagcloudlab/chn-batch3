#!/bin/bash
#
# Stop 3-node Cassandra cluster
#

CASSANDRA_LAB="$(cd "$(dirname "$0")" && pwd)"

RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}Stopping 3-Node Cassandra Cluster...${NC}"

for NODE_NUM in 3 2 1; do
    NODE_DIR="$CASSANDRA_LAB/cassandra-node-${NODE_NUM}"
    PID_FILE="$NODE_DIR/cassandra.pid"

    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        kill "$PID" 2>/dev/null
        rm -f "$PID_FILE"
        echo -e "${RED}  Node ${NODE_NUM} stopped (PID: ${PID}).${NC}"
    else
        echo -e "${RED}  Node ${NODE_NUM} was not running.${NC}"
    fi
done

# Cleanup any remaining Cassandra processes
pkill -f "org.apache.cassandra.service.CassandraDaemon" 2>/dev/null

echo -e "\n${YELLOW}Cluster is DOWN.${NC}"
