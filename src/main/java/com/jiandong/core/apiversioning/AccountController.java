package com.jiandong.core.apiversioning;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

	@GetMapping(value = "/{id}", version = "1.0")
	Account getAccount(@PathVariable String id) {
		return new Account(id, "default");
	}

	@GetMapping(path = "/{id}", version = "1.1+")
	Account getAccount1_1(@PathVariable String id) {
		return new Account(id, "advanced");
	}

	public record Account(String id, String name) {

	}

}
