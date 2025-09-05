package com.jiandong.integration;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

@Configuration
@EnableIntegration
public class IntegrationFlowConfig {

	private final Logger log = LoggerFactory.getLogger(IntegrationFlowConfig.this.getClass());

	@Bean
	public MessageChannel directChannel() {
		return new DirectChannel();
	}

	@Bean
	public MessageSource<Integer> inputs() {
		AtomicInteger ai = new AtomicInteger(0);
		return () -> new GenericMessage<>(ai.getAndIncrement());
	}

	@Bean
	public IntegrationFlow producerFlow() {
		return IntegrationFlow.from(inputs(), c -> c
						.poller(Pollers.fixedRate(60 * 60 * 1000))
						.id("producerFlowEndPoint")
				)
				.transform((GenericTransformer<Integer, String>) integer -> "=ab=" + integer)
				.channel(directChannel())
				.get();
	}

	@Bean
	public IntegrationFlow consumerFlow() {
		return IntegrationFlow.from(directChannel())
				.handle((payload, header) -> {
					log.info("payload={}", payload);
					return null;
				})
				.get();
	}

}