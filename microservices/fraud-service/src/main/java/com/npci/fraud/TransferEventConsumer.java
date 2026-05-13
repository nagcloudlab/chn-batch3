package com.npci.fraud;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.npci.event.TransferEvent;

@Service
public class TransferEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TransferEventConsumer.class);

    private static final double HIGH_VALUE_THRESHOLD = 5000.0;
    private static final double SUSPICIOUS_THRESHOLD = 50000.0;

    @KafkaListener(topics = "transfer-events", groupId = "fraud-group")
    public void onTransferEvent(ConsumerRecord<String, TransferEvent> record) {
        TransferEvent event = record.value();

        logger.info("========== FRAUD DETECTION SERVICE ==========");
        logger.info("Analyzing transfer event: key={}, partition={}, offset={}",
                record.key(), record.partition(), record.offset());
        logger.info("Event: {}", event);

        // Run fraud checks
        checkHighValueTransfer(event);
        checkSuspiciousTransfer(event);
        checkSelfTransfer(event);

        logger.info("Fraud analysis completed for eventId={}", event.getEventId());
        logger.info("=============================================");
    }

    private void checkHighValueTransfer(TransferEvent event) {
        if (event.getAmount() > HIGH_VALUE_THRESHOLD) {
            logger.warn("[ALERT] HIGH VALUE transfer detected: Rs.{} from {} to {} via {}",
                    event.getAmount(), event.getFromAccount(), event.getToAccount(), event.getPaymentMode());
        } else {
            logger.info("[OK] Amount Rs.{} is within normal range", event.getAmount());
        }
    }

    private void checkSuspiciousTransfer(TransferEvent event) {
        if (event.getAmount() > SUSPICIOUS_THRESHOLD) {
            logger.error("[FRAUD ALERT] SUSPICIOUS transfer: Rs.{} exceeds threshold of Rs.{}! From: {} To: {} Mode: {}",
                    event.getAmount(), SUSPICIOUS_THRESHOLD,
                    event.getFromAccount(), event.getToAccount(), event.getPaymentMode());
            // In real system: flag transaction, notify compliance team, block account
        }
    }

    private void checkSelfTransfer(TransferEvent event) {
        if (event.getFromAccount().equals(event.getToAccount())) {
            logger.warn("[ALERT] Self-transfer detected: account {} transferred Rs.{} to itself",
                    event.getFromAccount(), event.getAmount());
        }
    }

}
