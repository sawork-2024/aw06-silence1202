package com.micropos.products.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Data
@AllArgsConstructor
//@Embeddable
public class NestedSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String app;
    private String store;
    private String address_one;
    private String address_two;
    private String contact;
    private String tax;
    private String symbol;
    private String percentage;
    private String footer;
    private String img;

    public NestedSettings(){}
}

