package com.jiandong.mq.kafka;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("!test")
public class KafkaTopics {

	@Bean
	public NewTopic kafkaTopic1() {
		return TopicBuilder.name("kafka-topic1")
				.partitions(1)
				.replicas(1)
				.build();
	}

}
