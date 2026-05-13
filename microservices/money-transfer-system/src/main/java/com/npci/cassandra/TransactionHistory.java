package com.npci.cassandra;

// Uncomment when spring-boot-starter-data-cassandra is enabled in pom.xml

// import java.time.Instant;
// import java.util.UUID;
//
// import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
// import org.springframework.data.cassandra.core.mapping.Column;
// import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
// import org.springframework.data.cassandra.core.mapping.Table;
//
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
//
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// @Table("transaction_history")
// public class TransactionHistory {
//
//     @PrimaryKeyColumn(name = "account_number", type = PrimaryKeyType.PARTITIONED)
//     private String accountNumber;
//
//     @PrimaryKeyColumn(name = "timestamp", type = PrimaryKeyType.CLUSTERED,
//             ordering = org.springframework.data.cassandra.core.cql.Ordering.DESCENDING)
//     private Instant timestamp;
//
//     @PrimaryKeyColumn(name = "event_id", type = PrimaryKeyType.CLUSTERED)
//     private String eventId;
//
//     private Double amount;
//     private String type;
//
//     @Column("from_account")
//     private String fromAccount;
//
//     @Column("to_account")
//     private String toAccount;
//
//     @Column("payment_mode")
//     private String paymentMode;
//
//     private String status;
//
// }
