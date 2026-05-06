package com.npci.notification;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

// @Component("emailNotificationService")
@Service("emailNotificationService")
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

}
