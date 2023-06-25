package com.mjd507.jackson;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Truck extends Vehicle {
    private double payloadCapacity;
}
