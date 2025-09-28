package com.jiandong.core.jackson;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class VehicleTest {

	static Stream<Arguments> vehicleProvider() {
		Car car = new Car();
		car.setMake("Mercedes-Benz");
		car.setModel("S500");
		car.setTopSpeed(250.0);
		car.setSeatingCapacity(5);
		Truck truck = new Truck();
		truck.setMake("Isuzu");
		truck.setModel("NQR");
		truck.setPayloadCapacity(7500.0);
		return Stream.of(
				arguments(car, "car"),
				arguments(truck, "truck")
		);
	}

	@ParameterizedTest
	@MethodSource("vehicleProvider")
	void testVehicleJson(Vehicle vehicle, String type) {
		JsonMapper jsonMapper = new JsonMapper();
		String jsonStr = jsonMapper.writeValueAsString(vehicle);
		//{"type":"car","make":"Mercedes-Benz","model":"S500","seatingCapacity":5,"topSpeed":250.0}
		//{"type":"truck","make":"Isuzu","model":"NQR","payloadCapacity":7500.0}
		assertTrue(jsonStr.contains(type));
	}

}
