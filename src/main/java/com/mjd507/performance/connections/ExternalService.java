package com.mjd507.performance.connections;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalService {
    private final PersonRepository personRepository;
    @SneakyThrows
    public void call() {
        Thread.sleep(3000);
    }

    @SneakyThrows
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void runInNewTransaction() {
        log.info("{}", personRepository.findAll());
        Thread.sleep(4000);
    }

}
