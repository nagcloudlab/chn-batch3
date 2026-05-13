package com.npci.accounts.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.npci.accounts.dto.AccountDto;
import com.npci.accounts.entity.Account;
import com.npci.accounts.exception.AccountNotFoundException;
import com.npci.accounts.repository.AccountRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Account management endpoints")
public class AccountController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AccountController.class);

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    @Operation(summary = "Get all accounts")
    public List<Account> getAllAccounts() {
        logger.info("Fetching all accounts");
        return accountRepository.findAll();
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account by account number")
    public Account getAccount(@PathVariable String accountNumber) {
        logger.info("Fetching account: {}", accountNumber);
        return accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountDto dto) {
        logger.info("Creating account: {}", dto.getAccountNumber());
        Account account = new Account(dto.getAccountNumber(), dto.getAccountHolderName(), dto.getBalance());
        Account saved = accountRepository.save(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{accountNumber}")
    @Operation(summary = "Update an existing account (full replace)")
    public Account updateAccount(@PathVariable String accountNumber, @Valid @RequestBody AccountDto dto) {
        logger.info("Updating account: {}", accountNumber);
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        account.setAccountHolderName(dto.getAccountHolderName());
        account.setBalance(dto.getBalance());
        return accountRepository.save(account);
    }

    @PatchMapping("/{accountNumber}/balance")
    @Operation(summary = "Update account balance (used by transfer service)")
    public Account updateBalance(@PathVariable String accountNumber, @RequestBody java.util.Map<String, Double> body) {
        logger.info("Updating balance for account: {}", accountNumber);
        Account account = accountRepository.findById(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        account.setBalance(body.get("balance"));
        return accountRepository.save(account);
    }

    @DeleteMapping("/{accountNumber}")
    @Operation(summary = "Delete an account")
    public ResponseEntity<Void> deleteAccount(@PathVariable String accountNumber) {
        logger.info("Deleting account: {}", accountNumber);
        if (!accountRepository.existsById(accountNumber)) {
            throw new AccountNotFoundException("Account not found: " + accountNumber);
        }
        accountRepository.deleteById(accountNumber);
        return ResponseEntity.noContent().build();
    }

}
