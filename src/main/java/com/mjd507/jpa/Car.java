package com.mjd507.jpa;

import jakarta.persistence.*;
import lombok.Data;


@Entity(name = "car")
@Data
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String model;

    @Column
    private Integer year;

}