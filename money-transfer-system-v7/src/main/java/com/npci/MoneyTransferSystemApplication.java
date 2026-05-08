package com.npci;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.npci.service.TransferService;

@SpringBootApplication
public class MoneyTransferSystemApplication {

	private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(MoneyTransferSystemApplication.class);

	public static void main(String[] args) {

		logger.info("Starting MoneyTransferSystemApplication...");
		logger.info("-".repeat(50));
		// Init..
		ConfigurableApplicationContext context = SpringApplication.run(MoneyTransferSystemApplication.class, args);
		logger.info("-".repeat(50));

		// Use
		TransferService transferService = context.getBean(TransferService.class);
		transferService.transfer(10.0, "A001", "A002");

		// String bean1 = context.getBean("bean1", String.class);
		// logger.info("Bean1 from NpciChnConfiguration: {}", bean1);

		// Destroy..
		logger.info("-".repeat(50));
		logger.info("Shutting down MoneyTransferSystemApplication...");
		context.close();
		logger.info("-".repeat(50));

	}

}
