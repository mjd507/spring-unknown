package com.jiandong.core;

import com.jiandong.core.resilience.SpringRetry;
import com.jiandong.core.resilience.ThirtyPartyService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(classes = {
		SpringRetry.class, ThirtyPartyService.class
})
class SpringRetryTest {

	@Autowired SpringRetry springRetry;

	@MockitoSpyBean ThirtyPartyService thirtyPartyService;

	@Test
	void testSuccess_noRetry() {
		springRetry.callExternal("thirty");
		verify(thirtyPartyService).getInfo(anyString());
	}

	@Test
	void testFailed_withRetry() {
		Assertions.assertThatThrownBy(() -> springRetry.callExternal("abcd"))
				.isExactlyInstanceOf(ThirtyPartyService.UnexpectedException.class);

		verify(thirtyPartyService, times(3)).getInfo(anyString());
	}

}
