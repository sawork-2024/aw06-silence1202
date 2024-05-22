package com.micropos.products.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;


@Entity
@Data

@AllArgsConstructor

public class Settings {


    @Id
    private Long _id;
    private String id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nested_settings_id")
    private NestedSettings settings;


    public NestedSettings getSettings() {return settings;}
    public Settings(){}
}
