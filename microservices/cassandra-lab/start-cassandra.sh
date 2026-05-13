#!/bin/bash
#
# Start 3-node Cassandra cluster
# Node 1 (seed) starts first, then 2 and 3 join
#

CASSANDRA_LAB="$(cd "$(dirname "$0")" && pwd)"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}================================================${NC}"
echo -e "${CYAN}  Starting 3-Node Cassandra Cluster${NC}"
echo -e "${CYAN}================================================${NC}"

# Check nodes exist
if [ ! -d "$CASSANDRA_LAB/cassandra-node-1" ]; then
    echo -e "${YELLOW}Nodes not found. Run ./setup-cassandra.sh first.${NC}"
    exit 1
fi

# --- Node 1 (seed) ---
echo -e "\n${YELLOW}[1/3] Starting Node 1 (seed) on port 9042...${NC}"
CASSANDRA_LOG_DIR="$CASSANDRA_LAB/cassandra-node-1/logs" \
"$CASSANDRA_LAB/cassandra-node-1/bin/cassandra" -R -p "$CASSANDRA_LAB/cassandra-node-1/cassandra.pid" > /dev/null 2>&1

echo -e "${YELLOW}  Waiting for Node 1 to be ready...${NC}"
for i in $(seq 1 40); do
    "$CASSANDRA_LAB/cassandra-node-1/bin/cqlsh" localhost 9042 -e "DESCRIBE keyspaces;" > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}  Node 1 is UP!${NC}"
        break
    fi
    printf "."
    sleep 3
done
echo ""

# --- Node 2 ---
echo -e "${YELLOW}[2/3] Starting Node 2 on port 9043...${NC}"
CASSANDRA_LOG_DIR="$CASSANDRA_LAB/cassandra-node-2/logs" \
"$CASSANDRA_LAB/cassandra-node-2/bin/cassandra" -R -p "$CASSANDRA_LAB/cassandra-node-2/cassandra.pid" > /dev/null 2>&1
sleep 15
echo -e "${GREEN}  Node 2 started.${NC}"

# --- Node 3 ---
echo -e "${YELLOW}[3/3] Starting Node 3 on port 9044...${NC}"
CASSANDRA_LOG_DIR="$CASSANDRA_LAB/cassandra-node-3/logs" \
"$CASSANDRA_LAB/cassandra-node-3/bin/cassandra" -R -p "$CASSANDRA_LAB/cassandra-node-3/cassandra.pid" > /dev/null 2>&1
sleep 15
echo -e "${GREEN}  Node 3 started.${NC}"

# Check cluster status
echo -e "\n${YELLOW}Checking cluster status...${NC}"
sleep 5
"$CASSANDRA_LAB/cassandra-node-1/bin/nodetool" -p 7199 status

echo -e "\n${CYAN}================================================${NC}"
echo -e "${GREEN}  Cluster is UP!${NC}"
echo -e "${CYAN}================================================${NC}"
echo -e "  Node 1 (seed) : localhost:${CYAN}9042${NC}"
echo -e "  Node 2        : localhost:${CYAN}9043${NC}"
echo -e "  Node 3        : localhost:${CYAN}9044${NC}"
echo -e ""
echo -e "  CQL shell     : ${CYAN}./cassandra-node-1/bin/cqlsh localhost 9042${NC}"
echo -e "  Init schema   : ${CYAN}./cassandra-node-1/bin/cqlsh -f init-schema.cql${NC}"
echo -e "  Cluster status: ${CYAN}./cassandra-node-1/bin/nodetool -p 7199 status${NC}"
echo -e "${CYAN}================================================${NC}"
