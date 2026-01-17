package com.jiandong.mq.activemq;

import jakarta.jms.Destination;
import org.apache.activemq.command.ActiveMQQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsQueues {

	@Bean
	public Destination amq() {
		return new ActiveMQQueue("amq");
	}

	@Bean
	public Destination eipQueue() {
		return new ActiveMQQueue("eipQueue");
	}

}
