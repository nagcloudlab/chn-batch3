#!/bin/bash
#
# Stop a single Cassandra node (for Consistency Level demos)
# Usage: ./stop-node.sh <node-number>
# Example: ./stop-node.sh 3   => stops Node 3 (port 9044)
#

CASSANDRA_LAB="$(cd "$(dirname "$0")" && pwd)"
NODE_NUM=$1

RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m'

if [ -z "$NODE_NUM" ] || [ "$NODE_NUM" -lt 1 ] || [ "$NODE_NUM" -gt 3 ]; then
    echo "Usage: ./stop-node.sh <1|2|3>"
    exit 1
fi

NODE_DIR="$CASSANDRA_LAB/cassandra-node-${NODE_NUM}"
PID_FILE="$NODE_DIR/cassandra.pid"

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    kill "$PID" 2>/dev/null
    rm -f "$PID_FILE"
    echo -e "${RED}Node ${NODE_NUM} stopped (PID: ${PID}).${NC}"
else
    echo -e "${YELLOW}Node ${NODE_NUM} PID file not found. Trying to find process...${NC}"
    # Try JMX port to identify
    case $NODE_NUM in
        1) JMX=7199 ;;
        2) JMX=7299 ;;
        3) JMX=7399 ;;
    esac
    PID=$(lsof -ti:$JMX 2>/dev/null)
    if [ -n "$PID" ]; then
        kill "$PID" 2>/dev/null
        echo -e "${RED}Node ${NODE_NUM} stopped (PID: ${PID}).${NC}"
    else
        echo -e "${YELLOW}Node ${NODE_NUM} does not appear to be running.${NC}"
    fi
fi

echo -e "\n${GREEN}Now try:${NC}"
echo -e "  ${GREEN}CONSISTENCY ALL;${NC}    => should FAIL (only 2 of 3 nodes up)"
echo -e "  ${GREEN}CONSISTENCY QUORUM;${NC} => should WORK (2 of 3 = majority)"
echo -e "  ${GREEN}CONSISTENCY ONE;${NC}    => should WORK (1 of 2 available)"
