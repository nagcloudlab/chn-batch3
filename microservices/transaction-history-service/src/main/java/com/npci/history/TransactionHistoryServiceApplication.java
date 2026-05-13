package com.npci.history;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.npci")
public class TransactionHistoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionHistoryServiceApplication.class, args);
    }

}
