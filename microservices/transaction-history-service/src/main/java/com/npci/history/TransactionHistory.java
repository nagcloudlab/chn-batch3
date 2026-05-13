package com.npci.history;

import java.time.Instant;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table("transaction_history")
public class TransactionHistory {

    @PrimaryKeyColumn(name = "account_number", type = PrimaryKeyType.PARTITIONED)
    private String accountNumber;

    @PrimaryKeyColumn(name = "timestamp", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Instant timestamp;

    @PrimaryKeyColumn(name = "event_id", type = PrimaryKeyType.CLUSTERED)
    private String eventId;

    private Double amount;
    private String type;

    @Column("from_account")
    private String fromAccount;

    @Column("to_account")
    private String toAccount;

    @Column("payment_mode")
    private String paymentMode;

    private String status;

}
