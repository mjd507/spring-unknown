package com.jiandong.core.aspect;

import com.jiandong.core.aspect.SpringProxy.MyTransactional;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {SpringProxy.class, SpringProxyTest.TestService.class})
class SpringProxyTest {

	@Autowired
	TestService testService;

	@Test
	void testProxy() {
		// check log. create() method will be executed inside MyTransaction.
		testService.create();
		testService.add();

	}

	@Component
	static class TestService {

		private static final Logger log = LoggerFactory.getLogger(TestService.class);

		@MyTransactional
		public void create() {
			log.info("call method create");
		}

		public void add() {
			log.info("call method add");
		}

	}

}
