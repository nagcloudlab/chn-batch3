package com.npci.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.npci.entity.Account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

@SpringBootTest
public class JdbcAccountRepositoryTest {

    private JdbcAccountRepository accountRepository;

    @Autowired
    public JdbcAccountRepositoryTest(JdbcAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Test
    public void testLoadAccount() {

        // Act
        Account account = accountRepository.loadAccount("A001");

        // Assert
        assertNotNull(account);
        assertEquals("A001", account.getAccountNumber());
    }

}
