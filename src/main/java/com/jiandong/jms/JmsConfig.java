package com.jiandong.jms;

import jakarta.jms.Destination;
import jakarta.jms.Queue;
import org.apache.activemq.command.ActiveMQQueue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class JmsConfig {

	@Bean // Serialize message content to json using TextMessage
	public MessageConverter jacksonJsonMessageConverter() {
		JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

	@Bean
	public Destination amqDestination() {
		return new ActiveMQQueue("amq");
	}

}
