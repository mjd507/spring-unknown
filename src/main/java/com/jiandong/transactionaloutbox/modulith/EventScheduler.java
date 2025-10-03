package com.jiandong.transactionaloutbox.modulith;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventScheduler {

	private final IncompleteEventPublications incompleteEventPublications;

	public EventScheduler(IncompleteEventPublications incompleteEventPublications) {
		this.incompleteEventPublications = incompleteEventPublications;
	}

	// need distribute lock in multi instances
	@Scheduled(initialDelay = 5, fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
	public void runNonCompletedEvent() {
		incompleteEventPublications.resubmitIncompletePublicationsOlderThan(Duration.ofMinutes(3));
	}

}
