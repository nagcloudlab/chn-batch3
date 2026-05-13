package com.npci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MoneyTransferSystemApplication {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(MoneyTransferSystemApplication.class);

	public static void main(String[] args) {

		logger.info("Starting MoneyTransferSystemApplication...");
		logger.info("-".repeat(50));
		// Init..
		SpringApplication.run(MoneyTransferSystemApplication.class, args);
		logger.info("-".repeat(50));

	}

}
