package com.jiandong.performance.connections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExternalService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final PersonRepository personRepository;

	public ExternalService(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	public void call() {
		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void runInNewTransaction() {
		log.info("{}", personRepository.findAll());
		try {
			Thread.sleep(4000);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
