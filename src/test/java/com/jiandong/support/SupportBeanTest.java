package com.jiandong.support;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(SupportBean.class)
class SupportBeanTest {

	@Autowired SupportBean supportBean;

	@Test
	void test() {
		supportBean.ack("");
		supportBean.reject("");
		supportBean.randomNumber = 123;
	}

}
