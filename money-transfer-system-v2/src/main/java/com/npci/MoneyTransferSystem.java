package com.npci;

import org.slf4j.Logger;

import com.npci.repository.AccountRepository;
import com.npci.repository.AccountRepositoryFactory;
import com.npci.service.TransferService;
import com.npci.service.TransferServiceImpl;

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
        logger.info("Initializing components...");
        // Step 1: Create dependencies using factory
        AccountRepository jdbcAccountRepository = AccountRepositoryFactory.getAccountRepository("jdbc");
        AccountRepository jpaAccountRepository = AccountRepositoryFactory.getAccountRepository("jpa");
        // Step 2: Inject dependency via constructor (DI)
        TransferService transferService = new TransferServiceImpl(jpaAccountRepository);
        logger.info("Money Transfer System initialized.");
        logger.info("-".repeat(50));

        // use phase
        try {
            transferService.transfer(100.0, "ACC123", "ACC456");
            logger.info("-".repeat(30));
            transferService.transfer(200.0, "ACC123", "ACC789");
        } catch (IllegalArgumentException e) {
            logger.error("Transfer failed: {}", e.getMessage());
        }

        // shutdown phase
        logger.info("-".repeat(50));
        logger.info("Shutting down Money Transfer System...");
        logger.info("-".repeat(50));

    }
}