package com.jiandong.jms;

import java.util.concurrent.CountDownLatch;

import com.jiandong.support.SupportBean;
import com.jiandong.testcontainer.ActivemqConnectionConfiguration;
import com.jiandong.testcontainer.ActivemqContainerTest;
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
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {JmsService.class, JmsConfig.class, JmsQueues.class, SupportBean.class})
@ImportAutoConfiguration(classes = {ActivemqConnectionConfiguration.class, ActiveMQAutoConfiguration.class, JmsAutoConfiguration.class})
@DirtiesContext
class JmsServiceTest implements ActivemqContainerTest {

	private static final Logger log = LoggerFactory.getLogger(JmsServiceTest.class);

	@Autowired JmsService jmsService;

	@MockitoSpyBean SupportBean supportBean;

	@Autowired Destination amq;

	@Test
	void happyFlow() throws InterruptedException {
		// Given
		CountDownLatch latch = new CountDownLatch(1);
		doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(supportBean).ack(any());
		// When
		jmsService.send(amq, new JmsService.Email("jiandong@test.com", "hello~"));
		latch.await();
		// Then
		verify(supportBean).ack(any());
	}

}
