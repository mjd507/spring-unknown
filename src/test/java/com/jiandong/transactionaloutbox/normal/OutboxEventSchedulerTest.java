package com.jiandong.transactionaloutbox.normal;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.jiandong.lock.ShedLockConfig;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {OutboxEventScheduler.class, ShedLockConfig.class})
@ImportAutoConfiguration({DataSourceAutoConfiguration.class})
@DirtiesContext
class OutboxEventSchedulerTest {

	@Autowired
	OutboxEventScheduler outboxEventScheduler;

	@MockitoBean OutboxEventDao outboxEventDao;

	@MockitoBean NotifyService3 notifyService3;

	@Sql(scripts = {"classpath:shedlock/schema-h2.sql"})
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
