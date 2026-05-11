package com.npci.web;

import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.npci.dto.TransferRequestDto;
import com.npci.dto.TransferResponseDto;
import com.npci.service.TransferService;

@Controller
public class TransferController {

    private static final Logger logger = Logger.getLogger(TransferController.class.getName());

    private TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
        logger.info("TransferController initialized with TransferService");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/transfer")
    public String showTransferForm() {
        logger.info("Displaying transfer form");
        // authroization check
        return "transfer-form";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/transfer")
    public String processTransfer(@ModelAttribute TransferRequestDto transferRequest, Model model) {
        logger.info("Processing transfer request: " + transferRequest);
        transferService.transfer(
                transferRequest.getAmount(),
                transferRequest.getFromAccountNumber(),
                transferRequest.getToAccountNumber());
        TransferResponseDto response = new TransferResponseDto();
        response.setStatus("success");
        response.setMessage("Transfer completed successfully");
        model.addAttribute("response", response);
        return "transfer-status";
    }

}
