package com.npci.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.npci.event.TransferCompletedEvent;
import com.npci.model.Account;
import com.npci.notification.NotificationService;
import com.npci.repository.AccountRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

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
   public TransferServiceImpl(/* @Qualifier("jpaAccountRepository") */ AccountRepository accountRepository,
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

   @Value("#{T(java.lang.Runtime).getRuntime().availableProcessors()}")
   int numberOfCpuCores;

   @Value("#{systemProperties['user.home']}")
   private String userHome;

   @Value("#{mtsConfig.dbUrl}")
   private String dbUrl;

   @PostConstruct
   public void init() {
      logger.info("TransferServiceImpl init method called.");
      // Avaliable Cpu cores int cpuCores =
      // Runtime.getRuntime().availableProcessors();
      logger.info("Available CPU cores: {}", numberOfCpuCores);
      logger.info("User home directory: {}", userHome);
      logger.info("Database URL from MtsConfiguration: {}", dbUrl);
   }

   @PreDestroy
   public void cleanup() {
      logger.info("TransferServiceImpl cleanup method called.");
   }

   public void transfer(double amount, String fromAccount, String toAccount) {
      logger.info("Initiating transfer of ${} from {} to {}", amount, fromAccount, toAccount);

      // Approach 1 (v1): Don't create dependency here — tight coupling!
      // JdbcAccountRepository accountRepository = new JdbcAccountRepository();

      // Approach 2: Don't lookup from factory directly — still a hidden dependency!
      // AccountRepository accountRepository =
      // AccountRepositoryFactory.getAccountRepository("jpa");

      // Approach 3 (v2): Use injected dependency (via constructor) — loose coupling!

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
