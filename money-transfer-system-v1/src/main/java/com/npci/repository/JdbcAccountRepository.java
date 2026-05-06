package com.npci.repository;

import com.npci.model.Account;

/**
 * author: npci-dev1/team1
 */

public class JdbcAccountRepository {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JdbcAccountRepository.class);

    public JdbcAccountRepository() {
        // Initialize JDBC connection here
        logger.info("Initialized JDBC Account Repository");
    }

    public Account loadAccount(String accountNumber) {
        // Code to load account from database using JDBC
        Account account = new Account(accountNumber, "Unknown", 1000.00);
        logger.info("Loaded account details for account {}", accountNumber);
        return account;
    }

    public void updateAccount(Account account) {
        // Code to update account in database using JDBC
        logger.info("Updated account details for account {}", account.getAccountNumber());
    }

}
