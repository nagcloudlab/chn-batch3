package com.npci;

import org.slf4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.npci.config.MtsConfiguration;
import com.npci.service.TransferService;

@Configuration
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
        AnnotationConfigApplicationContext applicationContext = null;
        // applicationContext = new ClassPathXmlApplicationContext("mts-config.xml");
        applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(MtsConfiguration.class);
        applicationContext.getEnvironment().setActiveProfiles("dev");
        applicationContext.refresh(); // refresh context to apply new profile
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