package com.npci.history;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/history")
@Tag(name = "Transaction History", description = "Fetch transaction history from Cassandra")
public class TransactionHistoryController {

    private final TransactionHistoryRepository repository;

    public TransactionHistoryController(TransactionHistoryRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get transaction history for an account")
    public ResponseEntity<Map<String, Object>> getHistory(@PathVariable String accountNumber) {
        List<TransactionHistory> transactions = repository.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(Map.of(
                "accountNumber", accountNumber,
                "count", transactions.size(),
                "transactions", transactions));
    }

    @GetMapping
    @Operation(summary = "Get all transaction history records")
    public List<TransactionHistory> getAll() {
        return repository.findAll();
    }

}
