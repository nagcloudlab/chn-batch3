package com.npci.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.npci.model.Account;

/**
 * author: npci-dev1/team1
 */

// @Component("jdbcAccountRepository")
@Repository("jdbcAccountRepository")
@Primary
@Scope("singleton")
public class JdbcAccountRepository implements AccountRepository {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JdbcAccountRepository.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        // Initialize JDBC connection here
        logger.info("JdbcAccountRepository initialized with JdbcTemplate: {}", jdbcTemplate);
    }

    public Account loadAccount(String accountNumber) {
        // Code to load account from database using JDBC
        try {
            String sql = "SELECT number, balance FROM accounts WHERE number = ?";
            return jdbcTemplate.queryForObject(sql, new Object[] { accountNumber }, (rs, rowNum) -> {
                Account account = new Account();
                account.setAccountNumber(rs.getString("number"));
                account.setBalance(rs.getDouble("balance"));
                return account;
            });
        } catch (Exception e) {
            logger.error("Error loading account {}: {}", accountNumber, e.getMessage());
            throw new RuntimeException("Database error while loading account " + accountNumber, e);
        }
    }

    public void updateAccount(Account account) {
        // Code to update account in database using JDBC
        try {
            String sql = "UPDATE accounts SET balance = ? WHERE number = ?";
            jdbcTemplate.update(sql, account.getBalance(), account.getAccountNumber());
        } catch (Exception e) {
            logger.error("Error updating account {}: {}", account.getAccountNumber(), e.getMessage());
            throw new RuntimeException("Database error while updating account " + account.getAccountNumber(), e);
        }
    }

}
