package com.npci.repository;

import com.npci.entity.Account;

public interface AccountRepository {
    Account loadAccount(String accountNumber);

    void updateAccount(Account account);
}
