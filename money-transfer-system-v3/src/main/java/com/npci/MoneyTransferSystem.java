package com.npci;

import org.slf4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.npci.config.MtsConfiguration;
import com.npci.service.TransferService;

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
        // applicationContext = new ClassPathXmlApplicationContext("mts-config.xml");
        applicationContext = new AnnotationConfigApplicationContext(MtsConfiguration.class);
        logger.info("Money Transfer System initialized.");
        logger.info("-".repeat(50));

        // -------------------------------------------------
        // use phase
        // -------------------------------------------------
        TransferService transferService = applicationContext.getBean("transferService", TransferService.class);
        try {
            transferService.transfer(100.0, "ACC123", "ACC456");
            logger.info("-".repeat(30));
            transferService.transfer(200.0, "ACC123", "ACC789");
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