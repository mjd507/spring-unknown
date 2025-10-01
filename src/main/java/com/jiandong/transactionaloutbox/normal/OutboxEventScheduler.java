package com.jiandong.transactionaloutbox.normal;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OutboxEventScheduler {

	private final OutboxEventDao outboxEventDao;

	private final NotifyService notifyService;

	public OutboxEventScheduler(OutboxEventDao outboxEventDao, NotifyService notifyService) {
		this.outboxEventDao = outboxEventDao;
		this.notifyService = notifyService;
	}

	// need distribute lock in multi instances
	@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
	public void runNonCompletedEvent() {
		List<OutboxEvent> outboxEvents = outboxEventDao.listNonCompletedEventsForScheduler();
		for (OutboxEvent failedEvent : outboxEvents) {
			if (failedEvent != null) {
				notifyService.sendNotification(failedEvent);
			}
		}
	}

}
