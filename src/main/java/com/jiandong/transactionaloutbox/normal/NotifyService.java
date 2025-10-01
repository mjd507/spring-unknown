package com.jiandong.transactionaloutbox.normal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotifyService {

	private static final Logger log = LoggerFactory.getLogger(NotifyService.class);

	private static final String EVENT_TYPE = "USER_REGISTER_NOTIFY";

	private final OutboxEventDao outboxEventDao;

	public NotifyService(OutboxEventDao outboxEventDao) {
		this.outboxEventDao = outboxEventDao;
	}

	@Transactional
	public boolean sendNotification(OutboxEvent outboxEvent) {
		if (outboxEvent.eventType().equals(EVENT_TYPE)) {
			boolean completed = outboxEventDao.completeOutboxEvent(outboxEvent);
			String email = outboxEvent.eventBody().split(":")[1];
			// put in last step in case of duplicate sending
			if (email.contains("@abc.com")) {
				throw new RuntimeException("simulate sending error.");
			}
			log.info("sending email to users mailbox: {}", email);
			return completed;
		}
		return false;
	}

}
