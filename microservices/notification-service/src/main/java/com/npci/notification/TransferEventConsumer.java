package com.npci.notification;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.npci.event.TransferEvent;

@Service
public class TransferEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TransferEventConsumer.class);

    @KafkaListener(topics = "transfer-events", groupId = "notification-group")
    public void onTransferEvent(ConsumerRecord<String, TransferEvent> record) {
        TransferEvent event = record.value();

        logger.info("========== NOTIFICATION SERVICE ==========");
        logger.info("Received transfer event: key={}, partition={}, offset={}",
                record.key(), record.partition(), record.offset());
        logger.info("Event: {}", event);

        // Simulate sending notifications
        sendSmsNotification(event);
        sendEmailNotification(event);
        sendPushNotification(event);

        logger.info("All notifications sent for eventId={}", event.getEventId());
        logger.info("==========================================");
    }

    private void sendSmsNotification(TransferEvent event) {
        logger.info("[SMS] Dear customer, Rs.{} has been debited from your account {} via {}. Ref: {}",
                event.getAmount(), event.getFromAccount(), event.getPaymentMode(), event.getEventId());
        logger.info("[SMS] Dear customer, Rs.{} has been credited to your account {} via {}. Ref: {}",
                event.getAmount(), event.getToAccount(), event.getPaymentMode(), event.getEventId());
    }

    private void sendEmailNotification(TransferEvent event) {
        logger.info("[EMAIL] Transfer confirmation sent to account holders: {} -> {}, amount: Rs.{}",
                event.getFromAccount(), event.getToAccount(), event.getAmount());
    }

    private void sendPushNotification(TransferEvent event) {
        logger.info("[PUSH] Transfer of Rs.{} completed successfully via {}",
                event.getAmount(), event.getPaymentMode());
    }

}
