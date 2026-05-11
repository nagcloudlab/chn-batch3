package com.npci.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "transactions", schema = "public")
public class Transaction {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    @Temporal(jakarta.persistence.TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;
    @Column(name = "type")
    @Enumerated(jakarta.persistence.EnumType.STRING)
    private TransactionType type;
    @ManyToOne
    @JoinColumn(name = "account_number", referencedColumnName = "number")
    private Account account;

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", account=" + (account != null ? account.getAccountNumber() : "null") +
                '}';
    }

}
