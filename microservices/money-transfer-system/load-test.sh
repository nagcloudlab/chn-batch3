#!/bin/bash

# Load test script for money transfer REST API
# Generates 100 money transfer requests to test system performance

BASE_URL="http://localhost:9090/api/v1/transfers"
COUNT=100
DELAY=0.1  # Delay between requests in seconds (adjustable)

echo "Starting load test: $COUNT transfer requests to $BASE_URL"
echo "============================================================"

SUCCESS_COUNT=0
FAILURE_COUNT=0
START_TIME=$(date +%s%N)

for i in $(seq 1 $COUNT); do
    # Transfer from A001 to A002
    FROM_ACCOUNT="A001"
    TO_ACCOUNT="A002"
    AMOUNT="1.00"
    
    # Create JSON payload
    PAYLOAD=$(cat <<EOF
{
  "fromAccountNumber": "$FROM_ACCOUNT",
  "toAccountNumber": "$TO_ACCOUNT",
  "amount": $AMOUNT
}
EOF
)
    
    # Send request
    RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL" \
        -H "Content-Type: application/json" \
        -d "$PAYLOAD")
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    RESPONSE_BODY=$(echo "$RESPONSE" | head -n-1)
    
    if [ "$HTTP_CODE" = "201" ] || [ "$HTTP_CODE" = "200" ]; then
        ((SUCCESS_COUNT++))
        echo "[$i/$COUNT] ✓ SUCCESS (HTTP $HTTP_CODE) - $FROM_ACCOUNT → $TO_ACCOUNT : $AMOUNT"
    else
        ((FAILURE_COUNT++))
        echo "[$i/$COUNT] ✗ FAILED (HTTP $HTTP_CODE) - $FROM_ACCOUNT → $TO_ACCOUNT : $AMOUNT"
    fi
    
    # Add delay between requests
    sleep $DELAY
done

END_TIME=$(date +%s%N)
DURATION=$(echo "scale=3; ($END_TIME - $START_TIME) / 1000000000" | bc)

echo "============================================================"
echo "Load Test Summary:"
echo "  Total Requests: $COUNT"
echo "  Successful: $SUCCESS_COUNT"
echo "  Failed: $FAILURE_COUNT"
echo "  Success Rate: $(echo "scale=2; $SUCCESS_COUNT * 100 / $COUNT" | bc)%"
echo "  Total Duration: ${DURATION}s"
echo "  Avg Response Time: $(echo "scale=3; $DURATION * 1000 / $COUNT" | bc)ms"
