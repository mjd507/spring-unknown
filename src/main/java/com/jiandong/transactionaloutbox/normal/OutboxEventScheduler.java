package com.jiandong.transactionaloutbox.normal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventScheduler {

	private final OutboxEventDao outboxEventDao;

	private final NotifyService3 notifyService3;

	public OutboxEventScheduler(OutboxEventDao outboxEventDao, NotifyService3 notifyService3) {
		this.outboxEventDao = outboxEventDao;
		this.notifyService3 = notifyService3;
	}

	@SchedulerLock(name = "OutboxEvents-Normal")
	@Scheduled(initialDelay = 5, fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
	public void runNonCompletedEvent() {
		// To assert that the lock is held (prevents misconfiguration errors)
		LockAssert.assertLocked();
		List<OutboxEvent> outboxEvents = outboxEventDao.listNonCompletedEvents(LocalDateTime.now().minusMinutes(3));
		for (OutboxEvent failedEvent : outboxEvents) {
			notifyService3.sendNotification(failedEvent);
		}
	}

}
