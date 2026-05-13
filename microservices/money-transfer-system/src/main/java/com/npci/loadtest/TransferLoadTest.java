package com.npci.loadtest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Load test script to generate 100 money transfer requests
 * against the REST API
 */
public class TransferLoadTest {

    private static final String BASE_URL = "http://localhost:8181/api/v1/transfers";
    private static final int TOTAL_REQUESTS = 100;
    private static final int THREAD_POOL_SIZE = 10; // Concurrent threads
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static AtomicInteger successCount = new AtomicInteger(0);
    private static AtomicInteger failureCount = new AtomicInteger(0);
    private static long startTime;
    private static long endTime;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Load Test: " + TOTAL_REQUESTS + " transfer requests");
        System.out.println("Base URL: " + BASE_URL);
        System.out.println("Thread Pool Size: " + THREAD_POOL_SIZE);
        System.out.println("============================================================");

        startTime = System.nanoTime();
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        for (int i = 1; i <= TOTAL_REQUESTS; i++) {
            final int requestNum = i;
            executor.submit(() -> sendTransferRequest(requestNum));
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(5, TimeUnit.MINUTES);

        endTime = System.nanoTime();

        if (!finished) {
            System.out.println("Warning: Timeout waiting for all requests to complete");
            executor.shutdownNow();
        }

        printSummary();
    }

    private static void sendTransferRequest(int requestNum) {
        try {
            // Transfer from A001 to A002
            String fromAccount = "A001";
            String toAccount = "A002";
            double amount = 1.0;

            // Create JSON payload
            String jsonPayload = String.format(
                    "{\"fromAccountNumber\":\"%s\",\"toAccountNumber\":\"%s\",\"amount\":%.2f}",
                    fromAccount, toAccount, amount);

            // Build request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            // Send request
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                successCount.incrementAndGet();
                System.out.printf("[%d/%d] ✓ SUCCESS (HTTP %d) - %s → %s : %.2f%n",
                        requestNum, TOTAL_REQUESTS, response.statusCode(),
                        fromAccount, toAccount, amount);
            } else {
                failureCount.incrementAndGet();
                System.out.printf("[%d/%d] ✗ FAILED (HTTP %d) - %s → %s : %.2f%n",
                        requestNum, TOTAL_REQUESTS, response.statusCode(),
                        fromAccount, toAccount, amount);
            }
        } catch (Exception e) {
            failureCount.incrementAndGet();
            System.out.printf("[%d/%d] ✗ ERROR - %s%n", requestNum, TOTAL_REQUESTS, e.getMessage());
        }
    }

    private static void printSummary() {
        long duration = (endTime - startTime) / 1_000_000_000; // Convert to seconds
        int total = successCount.get() + failureCount.get();
        double successRate = (total > 0) ? (successCount.get() * 100.0 / total) : 0;
        double avgTime = (total > 0) ? (duration * 1000.0 / total) : 0;

        System.out.println("============================================================");
        System.out.println("Load Test Summary:");
        System.out.printf("  Total Requests: %d%n", total);
        System.out.printf("  Successful: %d%n", successCount.get());
        System.out.printf("  Failed: %d%n", failureCount.get());
        System.out.printf("  Success Rate: %.2f%%%n", successRate);
        System.out.printf("  Total Duration: %d seconds%n", duration);
        System.out.printf("  Average Response Time: %.2f ms%n", avgTime);
        System.out.println("============================================================");
    }
}
