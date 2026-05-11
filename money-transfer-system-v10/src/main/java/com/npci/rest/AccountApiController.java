package com.npci.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.npci.dto.AccountDto;
import com.npci.entity.Account;
import com.npci.exception.AccountNotFoundException;
import com.npci.repository.AccountRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Account management endpoints")
public class AccountApiController {

    private final AccountRepository accountRepository;

    public AccountApiController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    @Operation(summary = "Get all accounts")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account by account number")
    public Account getAccount(@PathVariable String accountNumber) {
        return accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountDto dto) {
        Account account = new Account(dto.getAccountNumber(), dto.getAccountHolderName(), dto.getBalance());
        Account saved = accountRepository.save(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{accountNumber}")
    @Operation(summary = "Update an existing account")
    public Account updateAccount(@PathVariable String accountNumber, @Valid @RequestBody AccountDto dto) {
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        account.setAccountHolderName(dto.getAccountHolderName());
        account.setBalance(dto.getBalance());
        return accountRepository.save(account);
    }

    @DeleteMapping("/{accountNumber}")
    @Operation(summary = "Delete an account")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        if (!accountRepository.existsById(accountNumber)) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        accountRepository.deleteById(accountNumber);
        return ResponseEntity.noContent().build();
    }

}
