package com.npci.notification;

import org.slf4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.npci.event.TransferCompletedEvent;

// @Component("emailNotificationService")
@Service("emailNotificationService")
@Scope("singleton")
public class EmailNotificationService implements NotificationService {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(EmailNotificationService.class);

    public EmailNotificationService() {
        logger.info("EmailNotificationService instance created.");
    }

    @Override
    public void sendNotification(String message) {
        // Simulate sending an email notification
        logger.info("Sending email notification: {}", message);
    }

    @EventListener
    public void handleTransferCompletedEvent(TransferCompletedEvent event) {
        logger.info("Received transfer event: {}", event);
        sendNotification("Transfer Event: " + event);
    }

}
