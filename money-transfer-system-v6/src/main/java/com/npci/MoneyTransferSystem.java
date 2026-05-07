package com.npci;

import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.npci.service.TransferService;

// @Configuration
// @ComponentScan(basePackages = "com.npci")
// @EnableAutoConfiguration
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class MoneyTransferSystem {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MoneyTransferSystem.class);

    public static void main(String[] args) {

        logger.info("Starting Money Transfer System...");

        // -------------------------------------------------
        // init/boot phase — main acts as the "assembler"
        // create components & wire them together
        // (in v3, Spring container will do this for us)
        // -------------------------------------------------
        logger.info("-".repeat(50));
        ConfigurableApplicationContext applicationContext = null;
        applicationContext = SpringApplication.run(MoneyTransferSystem.class, args);
        logger.info("Money Transfer System initialized.");
        // log active profiles
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        logger.info("Active Spring Profiles: {}", String.join(", ", activeProfiles));
        logger.info("-".repeat(50));

        // -------------------------------------------------
        // use phase
        // -------------------------------------------------

        TransferService transferService = applicationContext.getBean("transferService", TransferService.class);
        logger.info(transferService.getClass().getName());
        try {
            // transferService.transfer(10.0, "A001", "A002");
        } catch (IllegalArgumentException e) {
            logger.error("Transfer failed: {}", e.getMessage());
        }
        // -------------------------------------------------
        // shutdown phase
        // -------------------------------------------------
        logger.info("-".repeat(50));
        logger.info("Shutting down Money Transfer System...");
        logger.info("-".repeat(50));
        if (applicationContext != null) {
            applicationContext.close();
        }

    }
}