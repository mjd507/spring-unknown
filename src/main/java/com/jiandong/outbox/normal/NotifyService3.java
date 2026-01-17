package com.jiandong.outbox.normal;

import com.jiandong.support.SupportBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotifyService3 {

	private static final Logger log = LoggerFactory.getLogger(NotifyService3.class);

	private final OutboxEventDao outboxEventDao;

	private final SupportBean supportBean;

	public NotifyService3(OutboxEventDao outboxEventDao, SupportBean supportBean) {
		this.outboxEventDao = outboxEventDao;
		this.supportBean = supportBean;
	}

	@Transactional
	public void sendNotification(OutboxEvent outboxEvent) {
		outboxEventDao.completeOutboxEvent(outboxEvent);
		String email = outboxEvent.eventBody().split(":")[1];
		// put in last step in case of duplicate sending
		if (email.contains("@abc.com")) {
			supportBean.reject(outboxEvent);
			throw new RuntimeException("simulate sending error.");
		}
		log.info("sending email to users mailbox: {}", email);
		supportBean.ack(outboxEvent);
	}

}
