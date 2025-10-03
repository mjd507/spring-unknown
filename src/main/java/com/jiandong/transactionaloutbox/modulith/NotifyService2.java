package com.jiandong.transactionaloutbox.modulith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
public class NotifyService2 {

	private static final Logger log = LoggerFactory.getLogger(NotifyService2.class);

	@ApplicationModuleListener // from spring-modulith, async & transactional
	public void notify(RegisteredEvent registeredEvent) {
		String email = registeredEvent.email();
		// put in last step in case of duplicate sending
		if (email.contains("@abc.com")) {
			throw new RuntimeException("simulate sending error.");
		}
		log.info("sending email to users mailbox: {}", email);
	}

}
