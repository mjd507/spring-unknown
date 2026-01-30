package com.jiandong;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

// make this test run at last.
// due to shared middle-ware like mq are not clean.
// (messages consumes here but verified in other test class)
@Order(value = Integer.MAX_VALUE)
@Testcontainers(disabledWithoutDocker = true)
class ApplicationTest {

	@Test
	void contextLoad() {
		System.setProperty("spring.profiles.active", "test");
		Application.main(new String[] {});
	}

}
