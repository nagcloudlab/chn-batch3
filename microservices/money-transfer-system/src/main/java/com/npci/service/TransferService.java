package com.npci.service;

public interface TransferService {
    void transfer(double amount, String fromAccountNumber, String toAccountNumber);
}
