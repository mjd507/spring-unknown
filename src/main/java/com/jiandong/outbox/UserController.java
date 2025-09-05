package com.jiandong.outbox;

import ch.qos.logback.core.testUtil.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	private final Logger log = LoggerFactory.getLogger(UserController.this.getClass());

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping(value = "register")
	void register() {
		userService.register(new UserService.User(RandomUtil.getPositiveInt(), "JD_" + RandomUtil.getPositiveInt(), 1993));
	}

}
