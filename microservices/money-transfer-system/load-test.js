#!/usr/bin/env node

/**
 * Load test script for money transfer REST API
 * Generates 100 money transfer requests using Node.js
 * 
 * Prerequisites: npm install axios
 * Run: node load-test.js
 */

const http = require('http');
const https = require('https');
const url = require('url');

const BASE_URL = 'http://localhost:8181/api/v1/transfers';
const TOTAL_REQUESTS = 100;
const CONCURRENT_REQUESTS = 10;

let successCount = 0;
let failureCount = 0;
const startTime = Date.now();

function sendTransferRequest(requestNum) {
    return new Promise((resolve) => {
        try {
            // Transfer from A001 to A002
            const fromAccount = 'A001';
            const toAccount = 'A002';
            const amount = 1.0;

            const payload = JSON.stringify({
                fromAccountNumber: fromAccount,
                toAccountNumber: toAccount,
                amount: amount
            });

            const requestUrl = new URL(BASE_URL);
            const options = {
                hostname: requestUrl.hostname,
                port: requestUrl.port,
                path: requestUrl.pathname,
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': Buffer.byteLength(payload)
                },
                timeout: 10000
            };

            const req = http.request(options, (res) => {
                let data = '';
                res.on('data', (chunk) => { data += chunk; });
                res.on('end', () => {
                    if (res.statusCode === 201 || res.statusCode === 200) {
                        successCount++;
                        console.log(
                            `[${requestNum}/${TOTAL_REQUESTS}] ✓ SUCCESS (HTTP ${res.statusCode}) - ${fromAccount} → ${toAccount} : ${amount.toFixed(2)}`
                        );
                    } else {
                        failureCount++;
                        console.log(
                            `[${requestNum}/${TOTAL_REQUESTS}] ✗ FAILED (HTTP ${res.statusCode}) - ${fromAccount} → ${toAccount} : ${amount.toFixed(2)}`
                        );
                    }
                    resolve();
                });
            });

            req.on('error', (error) => {
                failureCount++;
                console.log(`[${requestNum}/${TOTAL_REQUESTS}] ✗ ERROR - ${error.message}`);
                resolve();
            });

            req.on('timeout', () => {
                req.destroy();
                failureCount++;
                console.log(`[${requestNum}/${TOTAL_REQUESTS}] ✗ TIMEOUT`);
                resolve();
            });

            req.write(payload);
            req.end();
        } catch (error) {
            failureCount++;
            console.log(`[${requestNum}/${TOTAL_REQUESTS}] ✗ ERROR - ${error.message}`);
            resolve();
        }
    });
}

async function runLoadTest() {
    console.log(`Starting Load Test: ${TOTAL_REQUESTS} transfer requests`);
    console.log(`Base URL: ${BASE_URL}`);
    console.log(`Concurrent Requests: ${CONCURRENT_REQUESTS}`);
    console.log('============================================================');

    // Send requests with concurrency control
    const batches = [];
    for (let i = 0; i < TOTAL_REQUESTS; i += CONCURRENT_REQUESTS) {
        const batch = [];
        for (let j = 0; j < CONCURRENT_REQUESTS && i + j < TOTAL_REQUESTS; j++) {
            batch.push(sendTransferRequest(i + j + 1));
        }
        batches.push(Promise.all(batch));
    }

    await Promise.all(batches);

    const endTime = Date.now();
    const duration = (endTime - startTime) / 1000;
    const total = successCount + failureCount;
    const successRate = total > 0 ? (successCount * 100 / total).toFixed(2) : 0;
    const avgTime = total > 0 ? (duration * 1000 / total).toFixed(2) : 0;

    console.log('============================================================');
    console.log('Load Test Summary:');
    console.log(`  Total Requests: ${total}`);
    console.log(`  Successful: ${successCount}`);
    console.log(`  Failed: ${failureCount}`);
    console.log(`  Success Rate: ${successRate}%`);
    console.log(`  Total Duration: ${duration.toFixed(3)} seconds`);
    console.log(`  Average Response Time: ${avgTime} ms`);
    console.log('============================================================');
}

// Make script executable
runLoadTest().catch(console.error);
