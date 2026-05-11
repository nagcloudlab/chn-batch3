package com.npci.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.npci.entity.Account;
import com.npci.exception.AccountNotFoundException;
import com.npci.exception.InsufficientBalanceException;
import com.npci.repository.AccountRepository;
import com.npci.repository.TransactionRepository;

import jakarta.annotation.PostConstruct;

@Service("transferService")
public class TransferServiceImpl implements TransferService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransferServiceImpl.class);

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private FooService fooService;

    @Value("${transfer.limit:10000}")
    private double transferLimit;

    @Autowired
    public TransferServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository,
            FooService fooService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.fooService = fooService;
        logger.info("TransferServiceImpl initialized with AccountRepository: {} and TransactionRepository: {}",
                accountRepository.getClass().getName(), transactionRepository.getClass().getName());
    }

    @PostConstruct
    public void init() {
        logger.info("TransferServiceImpl initialized with transfer limit: {}", transferLimit);
    }

    // ACID
    // Atomicity: The entire transfer operation is treated as a single unit of work.
    // If any part of the process fails (e.g., insufficient funds, account not
    // found), the entire transaction will be rolled back, ensuring that no partial
    // updates occur.
    // Consistency: The transfer operation ensures that the system remains in a
    // consistent state. For example, it checks for sufficient balance before
    // debiting the from account and ensures that both accounts are updated
    // correctly. If any validation fails, the transaction will be rolled back,
    // maintaining data integrity.
    // Isolation: The transaction is isolated from other concurrent transactions.
    // This means that while one transfer is being processed
    // (e.g., debiting and crediting accounts), other transactions will not see
    // intermediate states of the accounts involved in the transfer. This prevents
    // issues like dirty reads, non-repeatable reads, and phantom reads.
    // Durability: Once the transfer transaction is committed, the changes are
    // permanent and will survive any subsequent system failures. The updated
    // account balances and transaction records will be stored in the database,
    // ensuring that the transfer is not lost even if the system crashes immediately
    // after the transaction is completed.

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

        // Record transactions
        var withdrawalTransaction = new com.npci.entity.Transaction();
        withdrawalTransaction.setAccount(fromAccount);
        withdrawalTransaction.setAmount(amount);
        withdrawalTransaction.setType(com.npci.entity.TransactionType.WITHDRAWAL);
        withdrawalTransaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(withdrawalTransaction);
        logger.info("Recorded withdrawal transaction for account: {}", fromAccountNumber);

        var depositTransaction = new com.npci.entity.Transaction();
        depositTransaction.setAccount(toAccount);
        depositTransaction.setAmount(amount);
        depositTransaction.setType(com.npci.entity.TransactionType.DEPOSIT);
        depositTransaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(depositTransaction);
        logger.info("Recorded deposit transaction for account: {}", toAccountNumber);

        fooService.doWriteWithDatabase();

        logger.info("Transfer completed successfully");

    }

    @Override
    public List<com.npci.entity.Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findByAccountNumber(accountNumber);
    }

}
