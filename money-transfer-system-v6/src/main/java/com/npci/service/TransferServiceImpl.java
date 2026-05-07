package com.npci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.npci.event.TransferCompletedEvent;
import com.npci.model.Account;
import com.npci.notification.NotificationService;
import com.npci.repository.AccountRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

// POJO

/**
 * author: npci-dev1/team1
 */

// @Component("transferService")
@Service("transferService")
@Scope("singleton")
public class TransferServiceImpl implements TransferService {

   private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransferServiceImpl.class);

   private AccountRepository accountRepository;
   private NotificationService notification;
   private ApplicationEventPublisher eventPublisher;

   // @Autowired
   public TransferServiceImpl(@Qualifier("jdbcAccountRepository") AccountRepository accountRepository,
         ApplicationEventPublisher eventPublisher) {
      this.accountRepository = accountRepository;
      this.eventPublisher = eventPublisher;
      logger.info("TransferServiceImpl initialized with AccountRepository: {}",
            accountRepository.getClass().getSimpleName());
   }

   @Autowired(required = false)
   public void setNotification(NotificationService notification) {
      this.notification = notification;
      logger.info("NotificationService injected: {}",
            notification.getClass().getSimpleName());
   }

   @PostConstruct
   public void init() {
      logger.info("TransferServiceImpl init method called.");
   }

   @PreDestroy
   public void cleanup() {
      logger.info("TransferServiceImpl cleanup method called.");
   }

   // Cross-cutting concerns (aspects):
   // Auth..
   // Caching..
   // Logging..
   // Transaction management..
   // Exception handling..
   // Exporting metrics..
   // ...

   public void m1() {
      logger.info("Executing m1 method.");
   }

   @Transactional(transactionManager = "transactionManager", // Specify the transaction manager bean name
         rollbackFor = RuntimeException.class)
   public void transfer(double amount, String fromAccount, String toAccount) {

      logger.info("Initiating transfer of ${} from {} to {}", amount, fromAccount, toAccount);

      m1();

      // Load fromAccount details
      Account from = accountRepository.loadAccount(fromAccount);
      // Load toAccount details
      Account to = accountRepository.loadAccount(toAccount);
      // Check if fromAccount has sufficient balance
      if (from.getBalance() < amount) {
         logger.error("Transfer failed: Insufficient balance in account {}", fromAccount);
         throw new IllegalArgumentException("Insufficient balance");
      }
      // Deduct amount from fromAccount
      from.setBalance(from.getBalance() - amount);
      logger.info("Deducted ${} from account {}", amount, fromAccount);
      // Add amount to toAccount
      to.setBalance(to.getBalance() + amount);
      logger.info("Credited ${} to account {}", amount, toAccount);
      // Save updated account details
      accountRepository.updateAccount(from);

      boolean isFailed = true; // Simulate transfer failure for testing rollback
      if (isFailed) {
         logger.error("Simulated transfer failure. Throwing exception to trigger rollback.");
         throw new RuntimeException("Simulated transfer failure");
      }

      accountRepository.updateAccount(to);
      logger.info("Transfer of ${} from {} to {} completed successfully.", amount, fromAccount, toAccount);

      // Send notification
      if (notification != null) {
         // notification.sendNotification(
         // "Transfer of $" + amount + " from " + fromAccount + " to " + toAccount + "
         // completed successfully.");
      }

      // Publish transfer event
      eventPublisher.publishEvent(new TransferCompletedEvent(this, amount, fromAccount, toAccount));

   }

}
