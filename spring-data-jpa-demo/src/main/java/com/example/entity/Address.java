package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class Address {
    @Column(name = "customer_street")
    private String street;
    @Column(name = "customer_city")
    private String city;
    @Column(name = "customer_state")
    private String state;
    @Column(name = "customer_zip")
    private String zip;
}
