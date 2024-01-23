package com.mjd507.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/car/transaction")
@RequiredArgsConstructor
public class CarTransactionController {

    private final CarTransactionService carTransactionService;

    @GetMapping
    public void test() {
        carTransactionService.callB();
    }
}
