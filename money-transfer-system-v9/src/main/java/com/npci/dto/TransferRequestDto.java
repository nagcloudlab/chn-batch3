package com.npci.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransferRequestDto {

    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;

}
