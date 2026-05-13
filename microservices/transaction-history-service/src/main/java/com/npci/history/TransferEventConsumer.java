package com.npci.history;

import java.time.Instant;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.npci.event.TransferEvent;

@Service
public class TransferEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TransferEventConsumer.class);

    private final TransactionHistoryRepository repository;

    public TransferEventConsumer(TransactionHistoryRepository repository) {
        this.repository = repository;
    }

    // @KafkaListener(topics = "transfer-events", groupId = "history-group")
    public void onTransferEvent(ConsumerRecord<String, TransferEvent> record) {
        TransferEvent event = record.value();

        logger.info("========== TRANSACTION HISTORY SERVICE ==========");
        logger.info("Received transfer event: key={}, partition={}, offset={}",
                record.key(), record.partition(), record.offset());

        Instant now = Instant.now();

        // Save withdrawal record (partitioned by fromAccount)
        TransactionHistory withdrawal = TransactionHistory.builder()
                .accountNumber(event.getFromAccount())
                .timestamp(now)
                .eventId(event.getEventId())
                .amount(event.getAmount())
                .type("WITHDRAWAL")
                .fromAccount(event.getFromAccount())
                .toAccount(event.getToAccount())
                .paymentMode(event.getPaymentMode())
                .status(event.getStatus())
                .build();
        repository.save(withdrawal);
        logger.info("Saved WITHDRAWAL to Cassandra for account: {}", event.getFromAccount());

        // Save deposit record (partitioned by toAccount)
        TransactionHistory deposit = TransactionHistory.builder()
                .accountNumber(event.getToAccount())
                .timestamp(now)
                .eventId(event.getEventId())
                .amount(event.getAmount())
                .type("DEPOSIT")
                .fromAccount(event.getFromAccount())
                .toAccount(event.getToAccount())
                .paymentMode(event.getPaymentMode())
                .status(event.getStatus())
                .build();
        repository.save(deposit);
        logger.info("Saved DEPOSIT to Cassandra for account: {}", event.getToAccount());

        logger.info("=================================================");
    }

}
