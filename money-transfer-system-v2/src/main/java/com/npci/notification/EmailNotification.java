package com.npci.notification;

import org.slf4j.Logger;

public class EmailNotification implements Notification {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(EmailNotification.class);

    @Override
    public void sendNotification(String message) {
        // Simulate sending an email notification
        logger.info("Sending email notification: {}", message);
    }

}
