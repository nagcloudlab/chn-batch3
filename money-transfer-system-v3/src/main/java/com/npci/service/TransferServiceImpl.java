package com.npci.service;

import com.npci.model.Account;
import com.npci.notification.NotificationService;
import com.npci.repository.AccountRepository;

/**
 * author: npci-dev1/team1
 */

public class TransferServiceImpl implements TransferService {

   private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TransferServiceImpl.class);

   private final AccountRepository accountRepository;
   private final NotificationService notification;

   public TransferServiceImpl(AccountRepository accountRepository, NotificationService notification) {
      this.accountRepository = accountRepository;
      this.notification = notification;
      logger.info("TransferServiceImpl initialized with AccountRepository: {} and Notification: {}",
            accountRepository.getClass().getSimpleName(), notification.getClass().getSimpleName());
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
      notification.sendNotification(
            "Transfer of $" + amount + " from " + fromAccount + " to " + toAccount + " completed successfully.");
   }

}
