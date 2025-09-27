package com.jiandong.jms;

import java.util.function.Consumer;

import jakarta.jms.Destination;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsClient;
import org.springframework.stereotype.Service;

@Service
public class JmsService {

	private static final Logger log = LoggerFactory.getLogger(JmsService.class);

	private final JmsClient jmsClient;

	private @Nullable Consumer<Object> consumer;

	public JmsService(JmsClient jmsClient) {
		this.jmsClient = jmsClient;
	}

	public void setMsgConsumer(Consumer<Object> consumer) {
		this.consumer = consumer;
	}

	public void send(Destination destination, Object msg) {
		jmsClient.destination(destination).send(msg);
	}

	@JmsListener(destination = "amq")
	public void receive(Email email) {
		log.info("Received message: {}", email);
		if (consumer != null) {
			consumer.accept(email);
		}
	}

	public record Email(String to, String body) {

	}

}
