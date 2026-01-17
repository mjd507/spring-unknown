package com.jiandong.mq.kafka;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.jiandong.support.SupportBean;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

	private static final Logger log = LoggerFactory.getLogger(KafkaService.class);

	private final KafkaTemplate<String, Object> kafkaTemplate;

	private final SupportBean supportBean;

	private final JsonMapper jsonMapper = new JsonMapper();

	public KafkaService(KafkaTemplate<String, Object> kafkaTemplate, SupportBean supportBean) {
		this.kafkaTemplate = kafkaTemplate;
		this.supportBean = supportBean;
	}

	public void send(String topic, Object msg) throws ExecutionException, InterruptedException {
		if (!(msg instanceof String)) {
			msg = jsonMapper.writeValueAsString(msg);
		}
		CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send(topic, msg);
		SendResult<String, Object> stringObjectSendResult = send.get();
		log.info(stringObjectSendResult.getProducerRecord().toString());
		log.info(stringObjectSendResult.toString());
	}

	@KafkaListener(topics = "kafka-topic1", groupId = "group1")
	public void receive(ConsumerRecord<String, String> record) {
		String value = record.value();
		log.info("Received message: {}", value);
		supportBean.ack(jsonMapper.readValue(value, Email.class));
	}

	public record Email(String to, String body) {

	}

}
