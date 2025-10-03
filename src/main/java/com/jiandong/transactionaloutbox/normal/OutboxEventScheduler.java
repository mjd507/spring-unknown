package com.jiandong.transactionaloutbox.normal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

	// need distribute lock in multi instances
	@Scheduled(initialDelay = 5, fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
	public void runNonCompletedEvent() {
		List<OutboxEvent> outboxEvents = outboxEventDao.listNonCompletedEvents(LocalDateTime.now().minusMinutes(3));
		for (OutboxEvent failedEvent : outboxEvents) {
			if (failedEvent != null) {
				notifyService3.sendNotification(failedEvent);
			}
		}
	}

}
