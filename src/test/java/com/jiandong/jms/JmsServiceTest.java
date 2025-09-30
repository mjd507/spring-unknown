package com.jiandong.jms;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import jakarta.jms.Destination;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.activemq.autoconfigure.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jms.autoconfigure.JmsAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {JmsService.class, JmsConfig.class, JmsQueues.class})
@ImportAutoConfiguration(classes = {ActiveMQAutoConfiguration.class, JmsAutoConfiguration.class,})
@DirtiesContext
class JmsServiceTest {

	private static final Logger log = LoggerFactory.getLogger(JmsServiceTest.class);

	@Autowired JmsService jmsService;

	@Autowired Destination amq;

	@Test
	void happyFlow() throws InterruptedException {
		// Given
		CountDownLatch latch = new CountDownLatch(1);
		AtomicBoolean consumed = new AtomicBoolean(false);
		Consumer<Object> consumer = email -> {
			log.info("Consumed message: {}", email);
			consumed.set(true);
			latch.countDown();
		};
		jmsService.setMsgConsumer(consumer);
		// When
		jmsService.send(amq, new JmsService.Email("jiandong@test.com", "hello~"));
		latch.await();
		// Then
		assertThat(consumed).isTrue();
	}

}
