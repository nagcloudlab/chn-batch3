package com.npci.accounts.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.npci.accounts.entity.Account;

public interface AccountRepository extends JpaRepository<Account, String> {
}
