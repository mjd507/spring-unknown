package com.jiandong.jpa;

import jakarta.persistence.*;
import lombok.Data;


@Table(name = "car")
@Data
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long carId;

    @Column
    private String model;

    @Column
    private Integer carYear;

}