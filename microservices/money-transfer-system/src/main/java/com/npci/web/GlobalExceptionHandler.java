package com.npci.web;

import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.npci.exception.AccountNotFoundException;
import com.npci.exception.InsufficientBalanceException;

@ControllerAdvice(basePackages = "com.npci.web")
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccountNotFoundException.class)
    public String handleAccountNotFound(AccountNotFoundException ex, RedirectAttributes redirectAttributes) {
        logger.error("Account not found: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/transfer";
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public String handleInsufficientBalance(InsufficientBalanceException ex, RedirectAttributes redirectAttributes) {
        logger.error("Insufficient balance: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/transfer";
    }

    @ExceptionHandler(BindException.class)
    public String handleBindException(BindException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Binding/validation error: {}", ex.getMessage());
        StringBuilder errors = new StringBuilder();
        ex.getFieldErrors()
                .forEach(fe -> errors
                        .append(fe.getDefaultMessage() != null ? fe.getDefaultMessage() : fe.getField() + " is invalid")
                        .append(". "));
        if (errors.isEmpty()) {
            errors.append("Please check your input and try again.");
        }
        redirectAttributes.addFlashAttribute("errorMessage", errors.toString().trim());
        return "redirect:/transfer";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, RedirectAttributes redirectAttributes) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        return "redirect:/transfer";
    }

}
