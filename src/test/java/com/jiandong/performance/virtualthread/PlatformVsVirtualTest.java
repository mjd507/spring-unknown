package com.jiandong.performance.virtualthread;

import com.jiandong.support.ThirtyPartyService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PlatformVsVirtualTest {

	private static final Logger log = LoggerFactory.getLogger(PlatformVsVirtualTest.class);

	@InjectMocks PlatformVsVirtual platformVsVirtual;

	@Spy ThirtyPartyService thirtyPartyService;

	@Test
	void testPlatformVsVirtual() {
		int taskCount = 4;
		double platformExecutionTime = platformVsVirtual.platformExecutionTime(taskCount);
		double virtualExecutionTime = platformVsVirtual.virtualExecutionTime(taskCount);
		log.info("platformExecutionTime: {}", platformExecutionTime);
		log.info("virtualExecutionTime: {}", virtualExecutionTime);
		Assertions.assertThat(platformExecutionTime)
				.isGreaterThan(virtualExecutionTime);
		verify(thirtyPartyService, times(taskCount * 2)).slowMethod();
	}

}
