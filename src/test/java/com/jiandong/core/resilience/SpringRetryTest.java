package com.jiandong.core.resilience;

import com.jiandong.support.SupportBean;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(classes = {
		SpringRetry.class, SupportBean.class
})
@DirtiesContext
class SpringRetryTest {

	@Autowired SpringRetry springRetry;

	@MockitoSpyBean SupportBean supportBean;

	@Test
	void testSuccess_noRetry() {
		springRetry.callExternal("thirty");
		verify(supportBean, times(1)).unstableMethod(anyString());
	}

	@Test
	void testFailed_withRetry() {
		Assertions.assertThatThrownBy(() -> springRetry.callExternal("abcd"))
				.isExactlyInstanceOf(SupportBean.UnexpectedException.class);

		verify(supportBean, times(3)).unstableMethod(anyString());
	}

}
