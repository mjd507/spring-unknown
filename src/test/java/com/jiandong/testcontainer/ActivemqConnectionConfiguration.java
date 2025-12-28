package com.jiandong.testcontainer;

import org.springframework.boot.activemq.autoconfigure.ActiveMQConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivemqConnectionConfiguration {

	@Bean
	ActiveMQConnectionDetails activemqConnectionDetails() {
		return ActivemqContainerTest.connectionDetails();
	}

}
