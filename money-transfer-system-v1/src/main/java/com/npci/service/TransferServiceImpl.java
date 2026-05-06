package com.npci.service;

import com.npci.model.Account;
import com.npci.repository.JdbcAccountRepository;

public class TransferServiceImpl {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransferServiceImpl.class);

    public TransferServiceImpl() {
        logger.info("TransferServiceImpl instance created.");
    }

    public void transfer(double amount, String fromAccount, String toAccount) {
        logger.info("Initiating transfer of ${} from {} to {}", amount, fromAccount, toAccount);
        JdbcAccountRepository accountRepository = new JdbcAccountRepository();
        // Load fromAccount details
        Account from = accountRepository.loadAccount(fromAccount);
        // Load toAccount details
        Account to = accountRepository.loadAccount(toAccount);
        // Check if fromAccount has sufficient balance
        if (from.getBalance() < amount) {
            logger.error("Transfer failed: Insufficient balance in account {}", fromAccount);
            throw new IllegalArgumentException("Insufficient balance");
        }
        // Deduct amount from fromAccount
        from.setBalance(from.getBalance() - amount);
        logger.info("Deducted ${} from account {}", amount, fromAccount);
        // Add amount to toAccount
        to.setBalance(to.getBalance() + amount);
        logger.info("Credited ${} to account {}", amount, toAccount);
        // Save updated account details
        accountRepository.updateAccount(from);
        accountRepository.updateAccount(to);
        logger.info("Transfer of ${} from {} to {} completed successfully.", amount, fromAccount, toAccount);
    }

}
