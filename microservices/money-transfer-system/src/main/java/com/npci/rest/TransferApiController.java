package com.npci.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.npci.dto.TransferRequestDto;
import com.npci.dto.TransferResponseDto;
import com.npci.entity.Transaction;
import com.npci.service.TransferService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transfers")
@Tag(name = "Transfers", description = "Money transfer endpoints")
public class TransferApiController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(TransferApiController.class);

    private final TransferService transferService;

    public TransferApiController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @Operation(summary = "Transfer money between accounts")
    public ResponseEntity<TransferResponseDto> processTransfer(
            @Valid @RequestBody TransferRequestDto transferRequest) {
        logger.info("Processing transfer: from={} to={} amount={}",
                transferRequest.getFromAccountNumber(),
                transferRequest.getToAccountNumber(),
                transferRequest.getAmount());

        transferService.transfer(
                transferRequest.getAmount(),
                transferRequest.getFromAccountNumber(),
                transferRequest.getToAccountNumber());

        TransferResponseDto response = new TransferResponseDto();
        response.setStatus("SUCCESS");
        response.setMessage(String.format("Transferred %.2f from %s to %s",
                transferRequest.getAmount(),
                transferRequest.getFromAccountNumber(),
                transferRequest.getToAccountNumber()));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/history")
    @Operation(summary = "Get transaction history for an account")
    public ResponseEntity<Map<String, Object>> getTransactionHistory(
            @RequestParam String accountNumber) {
        logger.info("Fetching transaction history for account: {}", accountNumber);

        List<Transaction> transactions = transferService.getTransactionHistory(accountNumber);

        Map<String, Object> response = Map.of(
                "accountNumber", accountNumber,
                "count", transactions.size(),
                "transactions", transactions);

        return ResponseEntity.ok(response);
    }

}
