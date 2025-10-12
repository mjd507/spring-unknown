package com.jiandong.core.resilience;

import com.jiandong.support.SupportBean;

import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.stereotype.Component;

@EnableResilientMethods
@Component
public class SpringThrottling {

	private final SupportBean supportBean;

	public SpringThrottling(SupportBean supportBean) {
		this.supportBean = supportBean;
	}

	@ConcurrencyLimit(5)
	public void callExternal() {
		supportBean.slowMethod();
	}

}
