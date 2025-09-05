package com.jiandong.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MsgReceiver {

	private final Logger log = LoggerFactory.getLogger(MsgReceiver.this.getClass());

	@JmsListener(destination = "amq")
	public void receive(Email email) {
		log.info("Received < {} >", email);
	}

}
