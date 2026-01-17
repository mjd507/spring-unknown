package com.jiandong.integration;

import com.jiandong.mq.activemq.JmsConfig;
import com.jiandong.mq.activemq.JmsQueues;
import com.jiandong.testcontainer.ActivemqConnectionConfiguration;
import com.jiandong.testcontainer.ActivemqContainerTest;
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
@ImportAutoConfiguration(classes = {ActivemqConnectionConfiguration.class, ActiveMQAutoConfiguration.class, JmsAutoConfiguration.class})
@DirtiesContext
class JmsIntegrationFlowTest implements ActivemqContainerTest {

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
