package com.mjd507.jackson;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Car extends Vehicle {
    private int seatingCapacity;
    private double topSpeed;
}
