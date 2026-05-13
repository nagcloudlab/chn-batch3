package com.npci.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.npci.exception.AccountNotFoundException;

/**
 * REST client to call accounts-service via load-balanced RestClient.
 * Uses Eureka service discovery — "accounts-service" resolves to actual host:port.
 */
@Component
public class AccountServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceClient.class);

    private final RestClient restClient;

    public AccountServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://accounts-service")
                .build();
    }

    public AccountResponse getAccount(String accountNumber) {
        logger.info("Calling accounts-service: GET /api/v1/accounts/{}", accountNumber);
        try {
            return restClient.get()
                    .uri("/api/v1/accounts/{accountNumber}", accountNumber)
                    .retrieve()
                    .body(AccountResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
    }

    public AccountResponse updateBalance(String accountNumber, double newBalance) {
        logger.info("Calling accounts-service: PATCH /api/v1/accounts/{}/balance -> {}", accountNumber, newBalance);
        try {
            return restClient.patch()
                    .uri("/api/v1/accounts/{accountNumber}/balance", accountNumber)
                    .body(Map.of("balance", newBalance))
                    .retrieve()
                    .body(AccountResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
    }

}
