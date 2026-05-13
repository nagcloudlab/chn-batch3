# Load Test Scripts for Money Transfer API

This directory contains three different load test scripts to generate 100 money transfer requests against the REST API. Choose the one that best fits your workflow.

## Scripts Available

### 1. Shell Script (Bash/curl) - `load-test.sh`
**Best for:** Quick testing, minimal dependencies, simple metrics

**Prerequisites:**
- curl (usually pre-installed on macOS/Linux)
- bc (for calculations - may need to install: `brew install bc`)

**Usage:**
```bash
chmod +x load-test.sh
./load-test.sh
```

**Features:**
- 100 sequential transfer requests (A001 → A002)
- Configurable delay between requests (default 0.1s)
- Incrementing amounts for each request
- Real-time progress output
- Summary statistics (success rate, duration, average response time)

**Customize:**
Edit the script and change:
```bash
COUNT=100           # Number of requests
DELAY=0.1          # Delay between requests in seconds
BASE_URL=...       # API endpoint
```

---

### 2. Java Application - `src/main/java/com/npci/loadtest/TransferLoadTest.java`
**Best for:** Professional load testing, concurrent requests, high performance

**Prerequisites:**
- JDK 11+
- Project already compiled (or run `mvn clean compile`)

**Usage:**
```bash
# Compile if needed
mvn clean compile

# Run the load test
mvn exec:java -Dexec.mainClass="com.npci.loadtest.TransferLoadTest"

# Or compile and run directly
javac src/main/java/com/npci/loadtest/TransferLoadTest.java
java -cp src/main/java com.npci.loadtest.TransferLoadTest
```

**Features:**
- 100 concurrent transfer requests (10 threads by default)
- Non-blocking HTTP/2 client
- Configurable thread pool size
- Real-time progress with detailed logging
- Summary statistics

**Customize:**
Edit the class constants:
```java
private static final int TOTAL_REQUESTS = 100;
private static final int THREAD_POOL_SIZE = 10;  // Concurrent threads
```

---

### 3. Node.js Script - `load-test.js`
**Best for:** JavaScript ecosystem, integration with other tools, quick iteration

**Prerequisites:**
- Node.js 12+

**Usage:**
```bash
chmod +x load-test.js
node load-test.js
```

**Features:**
- 100 concurrent transfer requests (10 concurrent by default)
- Non-blocking HTTP requests
- Configurable concurrency
- Real-time progress output
- Summary statistics

**Customize:**
Edit the script constants:
```javascript
const TOTAL_REQUESTS = 100;
const CONCURRENT_REQUESTS = 10;
```

---

## API Endpoint Details

All scripts send POST requests to: `http://localhost:8181/api/v1/transfers`

**Request Payload:**
```json
{
  "fromAccountNumber": "A001",
  "toAccountNumber": "A002",
  "amount": 1.00
}
```

**Expected Response (Success - HTTP 201):**
```json
{
  "status": "SUCCESS",
  "message": "Transferred 150.50 from ACC001 to ACC002"
}
```

---

## Account Numbers Used

The scripts transfer money from **A001** to **A002** for all 100 requests:
- From: A001
- To: A002
- Amount: 1.00 rupee (fixed for each transfer)

---

## Performance Comparison

| Script | Setup Time | Execution Speed | Best Use Case |
|--------|-----------|-----------------|---------------|
| Shell | <1 min | Sequential (slower) | Quick testing, CI/CD |
| Java | 2-3 min | Parallel (faster) | Load testing, metrics |
| Node.js | 1-2 min | Parallel (faster) | Rapid iteration |

---

## Troubleshooting

### "Connection refused"
- Ensure the API server is running on `localhost:8181`
- Check server logs: `tail -f logs/*.log`

### "curl: command not found"
- Install curl: `brew install curl` (macOS) or `apt-get install curl` (Linux)

### Java compilation errors
- Ensure JDK 11+ is installed: `java -version`
- Run: `mvn clean compile`

### Node.js - "http module not found"
- Node.js is installed but missing http module (unlikely)
- Reinstall Node.js or try: `npm install`

---

## Example Output

```
Starting Load Test: 100 transfer requests
Base URL: http://localhost:8181/api/v1/transfers
============================================================
[1/100] ✓ SUCCESS (HTTP 201) - A001 → A002 : 1.00
[2/100] ✓ SUCCESS (HTTP 201) - A001 → A002 : 1.00
[3/100] ✓ SUCCESS (HTTP 201) - A001 → A002 : 1.00
...
[100/100] ✓ SUCCESS (HTTP 201) - A001 → A002 : 1.00
============================================================
Load Test Summary:
  Total Requests: 100
  Successful: 100
  Failed: 0
  Success Rate: 100.00%
  Total Duration: 2.345 seconds
  Average Response Time: 23.45 ms
============================================================
```

---

## Notes

- All scripts send 100 transfer requests from A001 to A002
- Each transfer is for 1.00 rupee (fixed amount)
- All three scripts generate identical request patterns for fair comparison
- Response times include network latency and server processing time
- For production testing, consider using dedicated load testing tools like JMeter or K6
