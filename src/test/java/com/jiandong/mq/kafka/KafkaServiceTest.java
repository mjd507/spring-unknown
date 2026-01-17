package com.jiandong.mq.kafka;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import com.jiandong.support.SupportBean;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.activemq.autoconfigure.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jms.autoconfigure.JmsAutoConfiguration;
import org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static com.jiandong.mq.kafka.KafkaServiceTest.TOPIC;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {KafkaService.class, SupportBean.class})
@ImportAutoConfiguration(classes = {KafkaAutoConfiguration.class, ActiveMQAutoConfiguration.class, JmsAutoConfiguration.class})
@EmbeddedKafka(partitions = 1, topics = TOPIC)
@DirtiesContext
class KafkaServiceTest {

	public static final String TOPIC = "kafka-topic1";

	@Autowired KafkaService kafkaService;

	@MockitoSpyBean SupportBean supportBean;

	@Test
	void happyFlow() throws InterruptedException, ExecutionException {
		// Given
		CountDownLatch latch = new CountDownLatch(1);
		doAnswer(_ -> {
			latch.countDown();
			return null;
		}).when(supportBean).ack(any());
		// When
		kafkaService.send(TOPIC, new KafkaService.Email("jiandong@test.com", "hello~"));
		latch.await();
		// Then
		verify(supportBean).ack(any());
	}

}
