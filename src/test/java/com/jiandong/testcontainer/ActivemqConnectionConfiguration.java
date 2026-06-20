package com.jiandong.testcontainer;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.springframework.boot.activemq.autoconfigure.ActiveMQConnectionDetails;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
public class ActivemqConnectionConfiguration {

	@Bean
	ActiveMQConnectionDetails connectionDetails() {
		return ActivemqContainerTest.connectionDetails();
	}

	@Bean
	ActiveMQConnectionFactory jmsConnectionFactory(ActiveMQConnectionDetails connectionDetails) {
		return new ActiveMQConnectionFactory(connectionDetails.getUser(),
				connectionDetails.getPassword(), connectionDetails.getBrokerUrl());
	}

}
