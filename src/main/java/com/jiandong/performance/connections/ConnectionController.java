package com.jiandong.performance.connections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("connection")
@RequiredArgsConstructor
public class ConnectionController {

    private final PersonService sampleService;
    private final ExternalService externalService;

    @GetMapping("/default-with-open-in-view")
    public void defaultOpenInView () {
        log.info("{}", sampleService.findAllUser());
        externalService.call();
    }

    @GetMapping("/default-auto-commit")
    public void defaultAutoCommit () {
        sampleService.defaultAutoCommit();
    }

    @GetMapping("/default-auto-commit2")
    public void defaultAutoCommit2 () {
        sampleService.defaultAutoCommit2();
    }

    @GetMapping("/run-in-new-transaction")
    public void newTransaction () {
        sampleService.twoTransactions();
    }
}
