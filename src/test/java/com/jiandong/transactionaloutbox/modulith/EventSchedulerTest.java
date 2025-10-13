package com.jiandong.transactionaloutbox.modulith;

import java.util.concurrent.CountDownLatch;

import com.jiandong.lock.ShedLockConfig;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {EventScheduler.class, ShedLockConfig.class})
@ImportAutoConfiguration({DataSourceAutoConfiguration.class})
@DirtiesContext
class EventSchedulerTest {

	@Autowired EventScheduler eventScheduler;

	@MockitoBean IncompleteEventPublications incompleteEventPublications;

	@Sql(scripts = {"classpath:shedlock/schema-h2.sql"})
	@Test
	void testScheduler() throws InterruptedException {
		doNothing().when(incompleteEventPublications).resubmitIncompletePublicationsOlderThan(any());
		int threadCount = 5;
		CountDownLatch latch = new CountDownLatch(threadCount);
		for (int i = 0; i < threadCount; i++) {
			Thread.ofVirtual().start(() -> {
				try {
					eventScheduler.runNonCompletedEvent();
				}
				finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		verify(incompleteEventPublications, times(1)).resubmitIncompletePublicationsOlderThan(any());
	}

}
