package com.jiandong.outbox.normal;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.jiandong.lock.ShedLockConfig;
import com.jiandong.testcontainer.PostgresContainerTest;
import com.jiandong.testcontainer.PostgresDataSourceConfiguration;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {OutboxEventScheduler.class, ShedLockConfig.class})
@ImportAutoConfiguration({PostgresDataSourceConfiguration.class})
@DirtiesContext
class OutboxEventSchedulerTest implements PostgresContainerTest {

	@Autowired
	OutboxEventScheduler outboxEventScheduler;

	@MockitoBean OutboxEventDao outboxEventDao;

	@MockitoBean NotifyService3 notifyService3;

	@Test
	void testScheduler() throws InterruptedException {
		when(outboxEventDao.listNonCompletedEvents(any())).thenReturn(List.of(new OutboxEvent(1, "", "", null, null)));
		int threadCount = 5;
		CountDownLatch latch = new CountDownLatch(threadCount);
		for (int i = 0; i < threadCount; i++) {
			Thread.ofVirtual().start(() -> {
				try {
					outboxEventScheduler.runNonCompletedEvent();
				}
				finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		verify(outboxEventDao, times(1)).listNonCompletedEvents(any());
		verify(notifyService3, times(1)).sendNotification(any());
	}

}
