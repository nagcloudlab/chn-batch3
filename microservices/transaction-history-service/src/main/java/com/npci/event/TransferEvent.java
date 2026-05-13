package com.npci.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransferEvent {

    private String eventId;
    private String fromAccount;
    private String toAccount;
    private double amount;
    private String status;
    private String paymentMode;
    private LocalDateTime timestamp;

}
