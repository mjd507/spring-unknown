package com.jiandong.jms;

import com.jiandong.support.SupportBean;
import jakarta.jms.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsClient;
import org.springframework.stereotype.Service;

@Service
public class JmsService {

	private static final Logger log = LoggerFactory.getLogger(JmsService.class);

	private final JmsClient jmsClient;

	private final SupportBean supportBean;

	public JmsService(JmsClient jmsClient, SupportBean supportBean) {
		this.jmsClient = jmsClient;
		this.supportBean = supportBean;
	}

	public void send(Destination destination, Object msg) {
		jmsClient.destination(destination).send(msg);
	}

	@JmsListener(destination = "amq")
	public void receive(Email email) {
		log.info("Received message: {}", email);
		supportBean.ack(email);
	}

	public record Email(String to, String body) {

	}

}
