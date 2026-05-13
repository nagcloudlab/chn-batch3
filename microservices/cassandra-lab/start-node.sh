#!/bin/bash
#
# Start a single node back (after stop-node.sh)
# Usage: ./start-node.sh <1|2>
#

CASSANDRA_LAB="$(cd "$(dirname "$0")" && pwd)"
NODE_NUM=$1
GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'

if [ -z "$NODE_NUM" ] || [ "$NODE_NUM" -lt 1 ] || [ "$NODE_NUM" -gt 2 ]; then
    echo "Usage: ./start-node.sh <1|2>"; exit 1
fi

NODE_DIR="$CASSANDRA_LAB/cassandra-node-${NODE_NUM}"
PORTS=("" "9042" "9043")

echo -e "${YELLOW}Starting Node ${NODE_NUM} on port ${PORTS[$NODE_NUM]}...${NC}"
CASSANDRA_LOG_DIR="$NODE_DIR/logs" "$NODE_DIR/bin/cassandra" -R -p "$NODE_DIR/cassandra.pid" > /dev/null 2>&1
sleep 20
echo -e "${GREEN}Node ${NODE_NUM} started.${NC}"
echo -e "${GREEN}Check: ./cassandra-node-1/bin/nodetool -p 7199 status${NC}"
