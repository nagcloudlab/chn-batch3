package com.npci.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.npci.client.AccountResponse;
import com.npci.client.AccountServiceClient;
import com.npci.event.TransferEvent;
import com.npci.exception.InsufficientBalanceException;

import jakarta.annotation.PostConstruct;

@Service("transferService")
public class TransferServiceImpl implements TransferService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransferServiceImpl.class);

    private final AccountServiceClient accountServiceClient;
    private final KafkaTemplate<String, TransferEvent> kafkaTemplate;

    private static final String TOPIC = "transfer-events";
    private static final List<String> PAYMENT_MODES = List.of("IMPS", "NEFT", "RTGS", "UPI");

    @Value("${transfer.limit:10000}")
    private double transferLimit;

    public TransferServiceImpl(AccountServiceClient accountServiceClient,
                               KafkaTemplate<String, TransferEvent> kafkaTemplate) {
        this.accountServiceClient = accountServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void init() {
        logger.info("TransferService initialized. Transfer limit: {}", transferLimit);
    }

    @Override
    public void transfer(double amount, String fromAccountNumber, String toAccountNumber) {

        logger.info("Initiating transfer: {} from {} to {}", amount, fromAccountNumber, toAccountNumber);

        // 1. Fetch accounts from accounts-service (load-balanced REST call via Eureka)
        AccountResponse fromAccount = accountServiceClient.getAccount(fromAccountNumber);
        AccountResponse toAccount = accountServiceClient.getAccount(toAccountNumber);

        // 2. Validate
        if (amount > transferLimit) {
            throw new IllegalArgumentException("Amount exceeds transfer limit of " + transferLimit);
        }
        if (fromAccount.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient funds in account: " + fromAccountNumber);
        }

        // 3. Update balances via accounts-service
        accountServiceClient.updateBalance(fromAccountNumber, fromAccount.getBalance() - amount);
        logger.info("Debited {} from {}. New balance: {}", amount, fromAccountNumber, fromAccount.getBalance() - amount);

        accountServiceClient.updateBalance(toAccountNumber, toAccount.getBalance() + amount);
        logger.info("Credited {} to {}. New balance: {}", amount, toAccountNumber, toAccount.getBalance() + amount);

        // 4. Publish transfer event to Kafka
        //    -> notification-service consumes (SMS, Email, Push)
        //    -> fraud-service consumes (fraud checks)
        //    -> transaction-history-service consumes (saves to Cassandra)
        String eventId = java.util.UUID.randomUUID().toString();
        String paymentMode = PAYMENT_MODES.get((int) (Math.random() * PAYMENT_MODES.size()));

        TransferEvent event = TransferEvent.builder()
                .eventId(eventId)
                .fromAccount(fromAccountNumber)
                .toAccount(toAccountNumber)
                .amount(amount)
                .status("SUCCESS")
                .paymentMode(paymentMode)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(TOPIC, paymentMode, event);
        logger.info("Published transfer event: topic={}, key={}, eventId={}", TOPIC, paymentMode, eventId);

        logger.info("Transfer completed successfully");
    }

}
