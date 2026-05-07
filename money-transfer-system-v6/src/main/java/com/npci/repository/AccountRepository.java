package com.npci.repository;

import com.npci.model.Account;

public interface AccountRepository {
    public Account loadAccount(String accountNumber);

    public void updateAccount(Account account);
}
