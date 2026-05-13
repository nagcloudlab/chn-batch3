#!/bin/bash
#
# Stop 2-node Cassandra cluster
#

CASSANDRA_LAB="$(cd "$(dirname "$0")" && pwd)"

RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}Stopping Cassandra Cluster...${NC}"

for NODE_NUM in 2 1; do
    PID_FILE="$CASSANDRA_LAB/cassandra-node-${NODE_NUM}/cassandra.pid"
    if [ -f "$PID_FILE" ]; then
        kill "$(cat "$PID_FILE")" 2>/dev/null
        rm -f "$PID_FILE"
        echo -e "${RED}  Node ${NODE_NUM} stopped.${NC}"
    else
        echo -e "${RED}  Node ${NODE_NUM} not running.${NC}"
    fi
done

pkill -f "org.apache.cassandra.service.CassandraDaemon" 2>/dev/null
echo -e "\n${YELLOW}Cluster is DOWN.${NC}"
