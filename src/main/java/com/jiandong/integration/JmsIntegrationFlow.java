package com.jiandong.integration;

import jakarta.jms.ConnectionFactory;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.jms.dsl.Jms;

@Configuration
@EnableIntegration
public class JmsIntegrationFlow {

	private static final JsonMapper jsonMapper = new JsonMapper();

	@Bean
	public IntegrationFlow messageDrivenFlow(ConnectionFactory connectionFactory) {
		return IntegrationFlow.from(Jms.messageDrivenChannelAdapter(connectionFactory)
						.destination("eipQueue"))
				.transform(String.class, source -> jsonMapper.readValue(source, Point.class))
				.handle(Point.class, (payload, headers) ->
						new Point(payload.x * 2, payload.y * 2))
				.channel(MessageChannels.queue("queueForDownstream"))
				.get();
	}

	public record Point(int x, int y) {

	}

}