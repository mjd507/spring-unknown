package com.jiandong.core.resilience;

import java.util.concurrent.atomic.AtomicInteger;

import com.jiandong.support.SupportBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@EnableResilientMethods
public class SpringRetry {

	private static final Logger log = LoggerFactory.getLogger(SpringRetry.class);

	private final SupportBean supportBean;

	private final AtomicInteger callTimes = new AtomicInteger(0);

	public SpringRetry(SupportBean supportBean) {
		this.supportBean = supportBean;
	}

	@Retryable(maxRetries = 2, delay = 1000, multiplier = 1.5) // 1000 1500 2250
	public void callExternal(String param) {
		int times = callTimes.getAndIncrement();
		log.info("starting calling thirty party service. times: {}", times);
		supportBean.unstableMethod(param);
	}

}
