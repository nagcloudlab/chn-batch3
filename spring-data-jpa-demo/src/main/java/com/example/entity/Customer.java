package com.example.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "customers", schema = "public")
public class Customer {
    @Id
    @Column(name = "customer_id")
    private Long id;
    @Column(name = "customer_name")
    private String name;
    @Column(name = "customer_gender")
    @Enumerated(EnumType.STRING)
    private CustomerGender gender;
    @Column(name = "customer_dob")
    @Temporal(jakarta.persistence.TemporalType.DATE)
    private LocalDate dob;
    @ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
    @CollectionTable(name = "customer_addresses", schema = "public", joinColumns = @jakarta.persistence.JoinColumn(name = "customer_id"))
    private List<Address> address = new java.util.ArrayList<>();
    @OneToMany(mappedBy = "customer")
    private List<Account> accounts = new java.util.ArrayList<>();

}
