package com.jiandong.performance.connections;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class PersonService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final PersonRepository personRepository;

	private final ExternalService externalService;

	private final TransactionTemplate transactionTemplate;

	public PersonService(PersonRepository personRepository, ExternalService externalService, TransactionTemplate transactionTemplate) {
		this.personRepository = personRepository;
		this.externalService = externalService;
		this.transactionTemplate = transactionTemplate;
	}

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
