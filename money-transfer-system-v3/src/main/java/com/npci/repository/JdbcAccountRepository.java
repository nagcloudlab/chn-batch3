package com.npci.repository;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.npci.model.Account;

/**
 * author: npci-dev1/team1
 */

// @Component("jdbcAccountRepository")
@Repository("jdbcAccountRepository")
public class JdbcAccountRepository implements AccountRepository {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JdbcAccountRepository.class);
    private final DataSource dataSource;

    @Autowired
    public JdbcAccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
        // Initialize JDBC connection here
        logger.info("JdbcAccountRepository initialized with DataSource: {}", dataSource);
    }

    public Account loadAccount(String accountNumber) {
        // Code to load account from database using JDBC

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            // Execute SQL query to fetch account details based on accountNumber
            // For demonstration, we will return a dummy account
        } catch (Exception e) {
            logger.error("Error loading account {}: {}", accountNumber, e.getMessage());
            throw new RuntimeException("Database error while loading account " + accountNumber, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.warn("Error closing database connection: {}", e.getMessage());
                }
            }
        }

        Account account = new Account(accountNumber, "Unknown", 1000.00);
        logger.info("Loaded account details for account {}", accountNumber);
        return account;
    }

    public void updateAccount(Account account) {
        // Code to update account in database using JDBC
        logger.info("Updated account details for account {}", account.getAccountNumber());
    }

}
