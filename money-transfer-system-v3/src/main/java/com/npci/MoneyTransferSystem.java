package com.npci;

import org.slf4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.npci.config.MtsConfiguration;
import com.npci.example.AppCache;
import com.npci.repository.AccountRepository;
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
        logger.info("-".repeat(50));

        // log active profiles
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        logger.info("Active Spring Profiles: {}", String.join(", ", activeProfiles));

        // -------------------------------------------------
        // use phase
        // -------------------------------------------------

        // AccountRepository accountRepository1 =
        // applicationContext.getBean("jdbcAccountRepository",
        // AccountRepository.class);
        // AccountRepository accountRepository2 =
        // applicationContext.getBean("jdbcAccountRepository",
        // AccountRepository.class);
        // logger.info("accountRepository1 == accountRepository2 ? {}",
        // accountRepository1 == accountRepository2);

        // AppCache appCache1 = applicationContext.getBean(AppCache.class);

        TransferService transferService = applicationContext.getBean("transferService", TransferService.class);

        try {
            transferService.transfer(100.0, "ACC123", "ACC456");
            // logger.info("-".repeat(30));
            // transferService.transfer(200.0, "ACC123", "ACC789");
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