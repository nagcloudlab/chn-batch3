package com.npci.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "accounts",schema = "public")
public class Account {

    @Id
    @Column(name = "number", nullable = false, unique = true)
    private String accountNumber;
    @Column(name = "holder_name")
    private String accountHolderName;
    private double balance;

}
