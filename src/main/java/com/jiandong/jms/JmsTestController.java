package com.jiandong.jms;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JmsTestController {

	private final MsgSender msgSender;

	public JmsTestController(MsgSender msgSender) {
		this.msgSender = msgSender;
	}

	@GetMapping("send")
	public void send() {
		msgSender.send();
	}

}