#!/bin/bash
#
# Start a single Cassandra node (to bring it back after stop-node.sh)
# Usage: ./start-node.sh <node-number>
# Example: ./start-node.sh 3   => starts Node 3 (port 9044)
#

CASSANDRA_LAB="$(cd "$(dirname "$0")" && pwd)"
NODE_NUM=$1

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

if [ -z "$NODE_NUM" ] || [ "$NODE_NUM" -lt 1 ] || [ "$NODE_NUM" -gt 3 ]; then
    echo "Usage: ./start-node.sh <1|2|3>"
    exit 1
fi

NODE_DIR="$CASSANDRA_LAB/cassandra-node-${NODE_NUM}"

if [ ! -d "$NODE_DIR" ]; then
    echo -e "${YELLOW}Node ${NODE_NUM} not found. Run ./setup-cassandra.sh first.${NC}"
    exit 1
fi

PORTS=("" "9042" "9043" "9044")

echo -e "${YELLOW}Starting Node ${NODE_NUM} on port ${PORTS[$NODE_NUM]}...${NC}"
CASSANDRA_LOG_DIR="$NODE_DIR/logs" \
"$NODE_DIR/bin/cassandra" -R -p "$NODE_DIR/cassandra.pid" > /dev/null 2>&1

sleep 15
echo -e "${GREEN}Node ${NODE_NUM} started on port ${PORTS[$NODE_NUM]}.${NC}"
echo -e "${GREEN}Check status: ./cassandra-node-1/bin/nodetool -p 7199 status${NC}"
