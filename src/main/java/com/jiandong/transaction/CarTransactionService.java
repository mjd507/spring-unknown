package com.jiandong.transaction;

import com.jiandong.jpa.Car;
import com.jiandong.jpa.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CarTransactionService {

    private final CarRepository carRepository;

    public void callB() {
        b();
    }

    @Transactional
    public void b() {
        Car car = new Car();
        car.setCarYear(2019);
        car.setModel("Tesla");
        carRepository.save(car);
        throw new RuntimeException();
    }
}
