package com.npci.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.npci.cassandra.TransactionHistoryRepository;
import com.npci.client.AccountResponse;
import com.npci.client.AccountServiceClient;
import com.npci.event.TransferEvent;
import com.npci.exception.AccountNotFoundException;
import com.npci.exception.InsufficientBalanceException;
import com.npci.repository.TransactionRepository;

import jakarta.annotation.PostConstruct;

@Service("transferService")
public class TransferServiceImpl implements TransferService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransferServiceImpl.class);

    private final AccountServiceClient accountServiceClient;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, TransferEvent> kafkaTemplate;
    private final TransactionHistoryRepository transactionHistoryRepository;

    private static final String TOPIC = "transfer-events";
    private static final List<String> PAYMENT_MODES = List.of("IMPS", "NEFT", "RTGS", "UPI", "FEE", "TAX", "REWARD", "LOAN");

    @Value("${transfer.limit:10000}")
    private double transferLimit;

    @Autowired
    public TransferServiceImpl(AccountServiceClient accountServiceClient,
                               TransactionRepository transactionRepository,
                               KafkaTemplate<String, TransferEvent> kafkaTemplate,
                               TransactionHistoryRepository transactionHistoryRepository) {
        this.accountServiceClient = accountServiceClient;
        this.transactionRepository = transactionRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.transactionHistoryRepository = transactionHistoryRepository;
    }

    @PostConstruct
    public void init() {
        logger.info("TransferServiceImpl initialized with transfer limit: {}", transferLimit);
    }

    @Override
    @Transactional
    public void transfer(double amount, String fromAccountNumber, String toAccountNumber) {

        logger.info("Initiating transfer of amount: {} from account: {} to account: {}",
                amount, fromAccountNumber, toAccountNumber);

        // 1. Fetch accounts from accounts-service (via load-balanced REST call)
        AccountResponse fromAccount = accountServiceClient.getAccount(fromAccountNumber);
        AccountResponse toAccount = accountServiceClient.getAccount(toAccountNumber);

        // 2. Validate balance
        if (fromAccount.getBalance() < amount) {
            logger.error("Insufficient funds in account: {}", fromAccountNumber);
            throw new InsufficientBalanceException("Insufficient funds");
        }

        // 3. Update balances via accounts-service
        accountServiceClient.updateBalance(fromAccountNumber, fromAccount.getBalance() - amount);
        logger.info("Debited {} from {}. New balance: {}", amount, fromAccountNumber, fromAccount.getBalance() - amount);

        accountServiceClient.updateBalance(toAccountNumber, toAccount.getBalance() + amount);
        logger.info("Credited {} to {}. New balance: {}", amount, toAccountNumber, toAccount.getBalance() + amount);

        // 4. Save to Cassandra
        String eventId = java.util.UUID.randomUUID().toString();
        String paymentMode = PAYMENT_MODES.get((int) (Math.random() * PAYMENT_MODES.size()));

        var cassandraWithdrawal = com.npci.cassandra.TransactionHistory.builder()
                .accountNumber(fromAccountNumber)
                .timestamp(java.time.Instant.now())
                .eventId(eventId)
                .amount(amount)
                .type("WITHDRAWAL")
                .fromAccount(fromAccountNumber)
                .toAccount(toAccountNumber)
                .paymentMode(paymentMode)
                .status("SUCCESS")
                .build();
        transactionHistoryRepository.save(cassandraWithdrawal);

        var cassandraDeposit = com.npci.cassandra.TransactionHistory.builder()
                .accountNumber(toAccountNumber)
                .timestamp(java.time.Instant.now())
                .eventId(eventId)
                .amount(amount)
                .type("DEPOSIT")
                .fromAccount(fromAccountNumber)
                .toAccount(toAccountNumber)
                .paymentMode(paymentMode)
                .status("SUCCESS")
                .build();
        transactionHistoryRepository.save(cassandraDeposit);
        logger.info("Saved transaction history to Cassandra");

        // 5. Publish event to Kafka
        TransferEvent event = TransferEvent.builder()
                .eventId(eventId)
                .fromAccount(fromAccountNumber)
                .toAccount(toAccountNumber)
                .amount(amount)
                .status("SUCCESS")
                .paymentMode(paymentMode)
                .timestamp(LocalDateTime.now())
                .build();

        String key = event.getPaymentMode();
        kafkaTemplate.send(TOPIC, key, event);
        logger.info("Published transfer event to Kafka: topic={}, key={}, eventId={}", TOPIC, key, eventId);

        logger.info("Transfer completed successfully");
    }

    @Override
    public List<com.npci.entity.Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findByAccountNumber(accountNumber);
    }

}
