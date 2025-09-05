package com.jiandong.performance.connections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("connection")
public class ConnectionController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final PersonService sampleService;

	private final ExternalService externalService;

	public ConnectionController(PersonService sampleService, ExternalService externalService) {
		this.sampleService = sampleService;
		this.externalService = externalService;
	}

	@GetMapping("/default-with-open-in-view")
	public void defaultOpenInView() {
		log.info("{}", sampleService.findAllUser());
		externalService.call();
	}

	@GetMapping("/default-auto-commit")
	public void defaultAutoCommit() {
		sampleService.defaultAutoCommit();
	}

	@GetMapping("/default-auto-commit2")
	public void defaultAutoCommit2() {
		sampleService.defaultAutoCommit2();
	}

	@GetMapping("/run-in-new-transaction")
	public void newTransaction() {
		sampleService.twoTransactions();
	}

}
