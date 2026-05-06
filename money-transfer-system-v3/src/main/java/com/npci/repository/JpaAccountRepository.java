package com.npci.repository;

import org.springframework.stereotype.Repository;

import com.npci.model.Account;

/**
 * author: npci-dev1/team1
 */

// @Component("jpaAccountRepository")
@Repository("jpaAccountRepository")
public class JpaAccountRepository implements AccountRepository {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JpaAccountRepository.class);

    public JpaAccountRepository() {
        // Initialize JPA EntityManager here
        logger.info("Initialized JPA Account Repository");
    }

    public Account loadAccount(String accountNumber) {
        // Code to load account from database using JPA
        Account account = new Account(accountNumber, "Unknown", 1000.00);
        logger.info("Loaded account details for account {}", accountNumber);
        return account;
    }

    public void updateAccount(Account account) {
        // Code to update account in database using JPA
        logger.info("Updated account details for account {}", account.getAccountNumber());
    }

}
