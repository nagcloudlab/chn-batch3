package com.npci.service;

import java.util.List;

import com.npci.entity.Transaction;

public interface TransferService {
    void transfer(double amount, String fromAccountNumber, String toAccountNumber);

    List<Transaction> getTransactionHistory(String accountNumber);
}
