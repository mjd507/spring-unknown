package com.jiandong;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.springframework.modulith.core.ApplicationModules;

@Order(1)
class ApplicationModuleTest {

	@Test
	void verifyAppModules() {
		ApplicationModules modules = ApplicationModules.of(Application.class);
		modules.forEach(System.out::println);
		modules.verify();
	}

}
