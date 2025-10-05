package com.jiandong.core.resilience;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {
		SpringThrottling.class, ThirtyPartyService.class
})
@EnableScheduling
@ImportAutoConfiguration({TaskSchedulingAutoConfiguration.class})
@DirtiesContext
class SpringThrottlingTest {

	private static final Logger log = LoggerFactory.getLogger(SpringThrottlingTest.class);

	private final List<String> threadNameList = Collections.synchronizedList(new ArrayList<>(100));

	@Autowired SpringThrottling springThrottling;

	@MockitoSpyBean ThirtyPartyService thirtyPartyService;

	@Autowired TaskScheduler taskScheduler;

	@Test
	void testThrottling() throws InterruptedException {
		// GIVEN
		int threadCount = 20;
		for (int i = 0; i < threadCount; i++) {
			Thread.ofVirtual()
					.start(() -> {
						springThrottling.callExternal();
						threadNameList.add(Thread.currentThread().getName());
					});
		}
		// print every 1500 mills
		taskScheduler.scheduleAtFixedRate(() ->
						log.info("running threads size: {}", threadNameList.size()),
				Duration.ofMillis(1500)
		);
		// WHEN
		while (threadNameList.size() != threadCount) {
			Thread.sleep(1000);
		}
		// THEN
		assertThat(threadNameList).hasSize(threadCount);
		verify(thirtyPartyService, times(threadCount)).slowMethod();
	}

}
