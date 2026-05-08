package com.npci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.npci.exception.AccountNotFoundException;
import com.npci.exception.InsufficientBalanceException;
import com.npci.repository.AccountRepository;

import jakarta.annotation.PostConstruct;

@Service("transferService")
public class TransferServiceImpl implements TransferService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransferServiceImpl.class);

    private AccountRepository accountRepository;

    @Value("${transfer.limit:10000}")
    private double transferLimit;

    @Autowired
    public TransferServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
        logger.info("TransferServiceImpl initialized with AccountRepository: {}",
                accountRepository.getClass().getName());
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

        var fromAccount = accountRepository.loadAccount(fromAccountNumber);
        if (fromAccount == null) {
            logger.error("From account not found: {}", fromAccountNumber);
            throw new AccountNotFoundException("From account not found");
        }
        var toAccount = accountRepository.loadAccount(toAccountNumber);
        if (toAccount == null) {
            logger.error("To account not found: {}", toAccountNumber);
            throw new AccountNotFoundException("To account not found");
        }

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

        accountRepository.updateAccount(fromAccount);
        logger.info("Updated from account: {}", fromAccountNumber);
        accountRepository.updateAccount(toAccount);
        logger.info("Updated to account: {}", toAccountNumber);

        logger.info("Transfer completed successfully");

    }

}
