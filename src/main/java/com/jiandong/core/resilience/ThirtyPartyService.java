package com.jiandong.core.resilience;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

@Service
public class ThirtyPartyService {

	private static final Logger log = LoggerFactory.getLogger(ThirtyPartyService.class);

	public void getInfo(String param) {
		if (param.contains("abc")) {
			throw new UnexpectedException();
		}
		log.info("retrieving data from thirty party service...");
	}

	public void slowMethod() {
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static class UnexpectedException extends RuntimeException {

	}

}
