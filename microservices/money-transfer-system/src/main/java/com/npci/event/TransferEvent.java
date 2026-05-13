package com.npci.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferEvent {

    private String eventId;
    private String fromAccount;
    private String toAccount;
    private double amount;
    private String status;
    private String paymentMode;
    private LocalDateTime timestamp;

}
