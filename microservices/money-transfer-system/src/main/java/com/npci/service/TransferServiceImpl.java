package com.npci.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.npci.cassandra.TransactionHistoryRepository;
import com.npci.entity.Account;
import com.npci.event.TransferEvent;
import com.npci.exception.AccountNotFoundException;
import com.npci.exception.InsufficientBalanceException;
import com.npci.repository.AccountRepository;
import com.npci.repository.TransactionRepository;

import jakarta.annotation.PostConstruct;

@Service("transferService")
public class TransferServiceImpl implements TransferService {

        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransferServiceImpl.class);

        private AccountRepository accountRepository;
        private TransactionRepository transactionRepository; // SQL
        private KafkaTemplate<String, TransferEvent> kafkaTemplate;
        private TransactionHistoryRepository transactionHistoryRepository; // CQL

        private static final String TOPIC = "transfer-events";
        private static final List<String> PAYMENT_MODES = List.of("IMPS", "NEFT", "RTGS", "UPI", "FEE", "TAX", "REWARD",
                        "LOAN");

        @Value("${transfer.limit:10000}")
        private double transferLimit;

        @Autowired
        public TransferServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository,
                        KafkaTemplate<String, TransferEvent> kafkaTemplate,
                        TransactionHistoryRepository transactionHistoryRepository) {
                this.accountRepository = accountRepository;
                this.transactionRepository = transactionRepository;
                this.kafkaTemplate = kafkaTemplate;
                this.transactionHistoryRepository = transactionHistoryRepository;
                logger.info("TransferServiceImpl initialized with AccountRepository: {} and TransactionRepository: {}",
                                accountRepository.getClass().getName(), transactionRepository.getClass().getName());
        }

        @PostConstruct
        public void init() {
                logger.info("TransferServiceImpl initialized with transfer limit: {}", transferLimit);
        }

        @Override
        @Transactional(transactionManager = "transactionManager", isolation = Isolation.READ_COMMITTED, rollbackFor = RuntimeException.class, noRollbackFor = AccountNotFoundException.class, timeout = 30, readOnly = false)
        public void transfer(double amount, String fromAccountNumber, String toAccountNumber) {

                logger.info("Initiating transfer of amount: {} from account: {} to account: {}",
                                amount, fromAccountNumber, toAccountNumber);

                Account fromAccount = accountRepository.findById(fromAccountNumber)
                                .orElseThrow(() -> {
                                        logger.error("From account not found: {}", fromAccountNumber);
                                        return new AccountNotFoundException("From account not found");
                                });

                Account toAccount = accountRepository.findById(toAccountNumber)
                                .orElseThrow(() -> {
                                        logger.error("To account not found: {}", toAccountNumber);
                                        return new AccountNotFoundException("To account not found");
                                });

                if (fromAccount.getBalance() < amount) {
                        logger.error("Insufficient funds in account: {}", fromAccountNumber);
                        throw new InsufficientBalanceException("Insufficient funds");
                }

                fromAccount.setBalance(fromAccount.getBalance() - amount);
                logger.info("Debited amount: {} from account: {}. New balance: {}",
                                amount, fromAccountNumber, fromAccount.getBalance());
                toAccount.setBalance(toAccount.getBalance() + amount);
                logger.info("Credited amount: {} to account: {}. New balance: {}",
                                amount, toAccountNumber, toAccount.getBalance());

                accountRepository.save(fromAccount);
                logger.info("Updated from account: {}", fromAccountNumber);
                accountRepository.save(toAccount);
                logger.info("Updated to account: {}", toAccountNumber);

                // // Record transactions
                // var withdrawalTransaction = new com.npci.entity.Transaction();
                // withdrawalTransaction.setAccount(fromAccount);
                // withdrawalTransaction.setAmount(amount);
                // withdrawalTransaction.setType(com.npci.entity.TransactionType.WITHDRAWAL);
                // withdrawalTransaction.setTimestamp(LocalDateTime.now());
                // transactionRepository.save(withdrawalTransaction);
                // logger.info("Recorded withdrawal transaction for account: {}",
                // fromAccountNumber);

                // var depositTransaction = new com.npci.entity.Transaction();
                // depositTransaction.setAccount(toAccount);
                // depositTransaction.setAmount(amount);
                // depositTransaction.setType(com.npci.entity.TransactionType.DEPOSIT);
                // depositTransaction.setTimestamp(LocalDateTime.now());
                // transactionRepository.save(depositTransaction);
                // logger.info("Recorded deposit transaction for account: {}", toAccountNumber);

                // ===== Cassandra: Save transaction history (uncomment when Cassandra is
                // enabled) =====
                String eventId = java.util.UUID.randomUUID().toString();

                // Save withdrawal record to Cassandra (partitioned by fromAccount)
                var cassandraWithdrawal = com.npci.cassandra.TransactionHistory.builder()
                                .accountNumber(fromAccountNumber)
                                .timestamp(java.time.Instant.now())
                                .eventId(eventId)
                                .amount(amount)
                                .type("WITHDRAWAL")
                                .fromAccount(fromAccountNumber)
                                .toAccount(toAccountNumber)
                                .paymentMode(PAYMENT_MODES.get((int) (Math.random() * PAYMENT_MODES.size())))
                                .status("SUCCESS")
                                .build();
                transactionHistoryRepository.save(cassandraWithdrawal);
                logger.info("Saved withdrawal to Cassandra for account: {}",
                                fromAccountNumber);

                // Save deposit record to Cassandra (partitioned by toAccount)
                var cassandraDeposit = com.npci.cassandra.TransactionHistory.builder()
                                .accountNumber(toAccountNumber)
                                .timestamp(java.time.Instant.now())
                                .eventId(eventId)
                                .amount(amount)
                                .type("DEPOSIT")
                                .fromAccount(fromAccountNumber)
                                .toAccount(toAccountNumber)
                                .paymentMode(cassandraWithdrawal.getPaymentMode())
                                .status("SUCCESS")
                                .build();
                transactionHistoryRepository.save(cassandraDeposit);
                logger.info("Saved deposit to Cassandra for account: {}", toAccountNumber);
                // ===== End Cassandra =====

                // Publish transfer event to Kafka as JSON
                // String eventId = java.util.UUID.randomUUID().toString();
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
