package com.jiandong.performance.connections;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final ExternalService externalService;
    private final TransactionTemplate transactionTemplate;

    @Transactional
    public List<Person> findAllUser() {
        return personRepository.findAll();
    }

    @Transactional
    public void defaultAutoCommit() {
        externalService.call();
        log.info("{}", personRepository.findAll());
    }

    public void defaultAutoCommit2() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            log.info("{}", personRepository.findAll());
        });
        externalService.call();
    }

    public void twoTransactions() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            log.info("{}", personRepository.findAll());
        });
        externalService.runInNewTransaction();
    }
}
