package com.jiandong.jackson;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Truck extends Vehicle {
    private double payloadCapacity;
}
