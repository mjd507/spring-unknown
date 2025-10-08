package com.jiandong.core.resilience;

import com.jiandong.support.ThirtyPartyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.stereotype.Component;

@EnableResilientMethods
@Component
public class SpringThrottling {

	private static final Logger log = LoggerFactory.getLogger(SpringThrottling.class);

	private final ThirtyPartyService thirtyPartyService;

	public SpringThrottling(ThirtyPartyService thirtyPartyService) {
		this.thirtyPartyService = thirtyPartyService;
	}

	@ConcurrencyLimit(5)
	public void callExternal() {
		thirtyPartyService.slowMethod();
	}

}
