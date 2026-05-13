#!/bin/bash
#
# Stop a single node (for CL demos)
# Usage: ./stop-node.sh <1|2>
#

CASSANDRA_LAB="$(cd "$(dirname "$0")" && pwd)"
NODE_NUM=$1
RED='\033[0;31m'; GREEN='\033[0;32m'; NC='\033[0m'

if [ -z "$NODE_NUM" ] || [ "$NODE_NUM" -lt 1 ] || [ "$NODE_NUM" -gt 2 ]; then
    echo "Usage: ./stop-node.sh <1|2>"; exit 1
fi

PID_FILE="$CASSANDRA_LAB/cassandra-node-${NODE_NUM}/cassandra.pid"
if [ -f "$PID_FILE" ]; then
    kill "$(cat "$PID_FILE")" 2>/dev/null; rm -f "$PID_FILE"
    echo -e "${RED}Node ${NODE_NUM} stopped.${NC}"
else
    echo -e "${RED}Node ${NODE_NUM} not running.${NC}"
fi

echo -e "\n${GREEN}CL Demo (RF=2, 1 node down):${NC}"
echo -e "  CONSISTENCY ONE;    => ${GREEN}WORKS${NC} (1 of 1 available)"
echo -e "  CONSISTENCY ALL;    => ${RED}FAILS${NC} (only 1 of 2 nodes available)"
