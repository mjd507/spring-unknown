package com.jiandong.jms;

import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MsgSender {

	private final JmsMessagingTemplate jmsMessagingTemplate;

	public MsgSender(JmsMessagingTemplate jmsMessagingTemplate) {
		this.jmsMessagingTemplate = jmsMessagingTemplate;
	}

	public void send() {
		jmsMessagingTemplate.convertAndSend("amq", new Email("info@example.com", "Hello"));
	}

}
