package com.npci.repository;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.npci.entity.Account;

@Repository("jdbcAccountRepository")
public class JdbcAccountRepository implements AccountRepository {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JdbcAccountRepository.class);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        logger.info("JdbcAccountRepository initialized with JdbcTemplate: {}", jdbcTemplate.getClass().getName());
    }

    @Override
    public Account loadAccount(String accountNumber) {
        logger.info("Loading account with account number: {}", accountNumber);
        String sql = "SELECT number, holder_name, balance FROM accounts WHERE number = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Account account = new Account();
            account.setAccountNumber(rs.getString("number"));
            account.setAccountHolderName(rs.getString("holder_name"));
            account.setBalance(rs.getDouble("balance"));
            return account;
        }, accountNumber);
    }

    @Override
    public void updateAccount(Account account) {
        logger.info("Updating account with account number: {}", account.getAccountNumber());
        String sql = "UPDATE accounts SET holder_name = ?, balance = ? WHERE number = ?";
        jdbcTemplate.update(sql, account.getAccountHolderName(), account.getBalance(), account.getAccountNumber());
    }

}
