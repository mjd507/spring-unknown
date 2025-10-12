package com.jiandong;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

// make this test run at last.
// due to shared middle-ware like mq are not clean.
// (messages consumes here but verified in other test class)
@Order(value = Integer.MAX_VALUE)
class ApplicationTest {

	@Test
	void contextLoad() {
		Application.main(new String[] {});
	}

}
