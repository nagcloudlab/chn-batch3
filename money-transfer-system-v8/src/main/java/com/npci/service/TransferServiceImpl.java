package com.npci.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    @Value("${transfer.limit:10000}")
    private double transferLimit;

    @Autowired
    public TransferServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        logger.info("TransferServiceImpl initialized with AccountRepository: {} and TransactionRepository: {}",
                accountRepository.getClass().getName(), transactionRepository.getClass().getName());
    }

    @PostConstruct
    public void init() {
        logger.info("TransferServiceImpl initialized with transfer limit: {}", transferLimit);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = RuntimeException.class)
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

        logger.info("Transfer completed successfully");

    }

    @Override
    public List<com.npci.entity.Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findByAccountNumber(accountNumber);
    }

}
