package com.jiandong.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.jiandong.support.SupportUtils.threadSleep;

@Component
public class SupportBean {

	private static final Logger log = LoggerFactory.getLogger(SupportBean.class);

	public int randomNumber = 123;

	public void ack(Object message) {
		log.info("{}", message);
	}

	public void reject(Object message) {
		log.info("{}", message);
	}

	public void unstableMethod(String param) {
		if (param.contains("abc")) {
			throw new UnexpectedException();
		}
		log.info("retrieving data from thirty party service...");
	}

	public void slowMethod() {
		threadSleep(1000);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void innerLongTransaction() {
		threadSleep(1000);
	}

	public static class UnexpectedException extends RuntimeException {

	}

}
