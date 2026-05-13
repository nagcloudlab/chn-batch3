package com.npci.history;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;

public interface TransactionHistoryRepository extends CassandraRepository<TransactionHistory, String> {

    List<TransactionHistory> findByAccountNumber(String accountNumber);

}
