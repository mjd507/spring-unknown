package com.jiandong.core.aspect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(classes = {LogAspect.class, LogAspectTest.TestClass.class})
@EnableAspectJAutoProxy
class LogAspectTest {

	@Autowired TestClass testClass;

	@Test
	void testAspect() {
		String abc = testClass.test("abc");
		Assertions.assertEquals("ABC", abc);
	}

	@Configuration
	static class TestClass {

		@Log
		String test(String name) {
			// do nothing, check logs from aspect
			return name.toUpperCase();
		}

	}

}
