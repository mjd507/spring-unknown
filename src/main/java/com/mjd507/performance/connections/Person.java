package com.mjd507.performance.connections;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "person")
@Data
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    @Column
    private String name;

}
