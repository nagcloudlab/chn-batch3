package com.npci.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.npci.dto.TransferRequestDto;
import com.npci.entity.Transaction;
import com.npci.service.TransferService;

import jakarta.validation.Valid;

@Controller
public class TransferController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransferController.class);

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
        logger.info("TransferController initialized with TransferService");
    }

    @org.springframework.web.bind.annotation.InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        // Allow empty string -> null for Double fields, so @NotNull validation works
        binder.registerCustomEditor(Double.class,
                new org.springframework.beans.propertyeditors.CustomNumberEditor(Double.class, true));
    }

    @GetMapping("/transfer")
    public String showTransferForm(Model model) {
        logger.info("Displaying transfer form");
        if (!model.containsAttribute("transferRequest")) {
            model.addAttribute("transferRequest", new TransferRequestDto());
        }
        return "transfer-form";
    }

    @PostMapping("/transfer")
    public String processTransfer(@Valid @ModelAttribute("transferRequest") TransferRequestDto transferRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        logger.info("Processing transfer request: from={} to={} amount={}",
                transferRequest.getFromAccountNumber(),
                transferRequest.getToAccountNumber(),
                transferRequest.getAmount());

        if (bindingResult.hasErrors()) {
            logger.warn("Validation errors: {}", bindingResult.getAllErrors());
            // Return form view directly so BindingResult + field errors are preserved
            return "transfer-form";
        }

        transferService.transfer(
                transferRequest.getAmount(),
                transferRequest.getFromAccountNumber(),
                transferRequest.getToAccountNumber());

        redirectAttributes.addFlashAttribute("successMessage",
                String.format("Successfully transferred %.2f from %s to %s",
                        transferRequest.getAmount(),
                        transferRequest.getFromAccountNumber(),
                        transferRequest.getToAccountNumber()));

        return "redirect:/transfer/success";
    }

    @GetMapping("/transfer/success")
    public String showSuccess(Model model) {
        if (!model.containsAttribute("successMessage")) {
            return "redirect:/transfer";
        }
        return "transfer-status";
    }

    @GetMapping("/transfer-history")
    public String showHistoryForm() {
        return "transfer-history";
    }

    @GetMapping("/transfer-history/search")
    public String searchHistory(@RequestParam("accountNumber") String accountNumber, Model model) {
        logger.info("Fetching transaction history for account: {}", accountNumber);
        List<Transaction> transactions = transferService.getTransactionHistory(accountNumber);
        model.addAttribute("transactions", transactions);
        model.addAttribute("accountNumber", accountNumber);
        return "transfer-history";
    }

}
