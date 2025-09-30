package com.jiandong.integration;

import com.jiandong.jms.JmsConfig;
import com.jiandong.jms.JmsQueues;
import jakarta.jms.Destination;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.activemq.autoconfigure.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jms.autoconfigure.JmsAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.jms.core.JmsClient;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = {JmsIntegrationFlow.class, JmsConfig.class, JmsQueues.class})
@ImportAutoConfiguration(classes = {ActiveMQAutoConfiguration.class, JmsAutoConfiguration.class})
@DirtiesContext
class JmsIntegrationFlowTest {

	@Autowired Destination eipQueue;

	@Autowired JmsClient jmsClient;

	@Qualifier("queueForDownstream")
	@Autowired QueueChannel queueChannel;

	@Test
	void happyFlow() {
		// Given
		jmsClient.destination(eipQueue)
				.send(new JmsIntegrationFlow.Point(1, 2));
		// When
		Message<?> message = queueChannel.receive();
		// Then
		Assertions.assertThat(message)
				.isNotNull()
				.extracting(Message::getPayload)
				.isInstanceOf(JmsIntegrationFlow.Point.class)
				.isEqualTo(new JmsIntegrationFlow.Point(2, 4));
	}

}
