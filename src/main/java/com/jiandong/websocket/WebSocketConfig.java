package com.jiandong.websocket;

import java.security.Principal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private static final String APP_PREFIX = "/app/";

	private static final String BROKER_TOPIC = "/topic";

	private static final String BROKER_QUEUE = "/queue";

	private static final String CONNECT_URL = "/websocket/connect";

	private static final String SUBSCRIBE_URL = "/messages";

	private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(CONNECT_URL)
				.setAllowedOriginPatterns("*")
				.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.setApplicationDestinationPrefixes(APP_PREFIX);
		config.enableSimpleBroker(BROKER_TOPIC, BROKER_QUEUE);
	}

	@Controller
	static class WebSocketController {

		private final SimpMessagingTemplate simpMessagingTemplate;

		public WebSocketController(SimpMessagingTemplate simpMessagingTemplate) {
			this.simpMessagingTemplate = simpMessagingTemplate;
		}

		@MessageMapping("/send-to-all") // client to application
		@SendTo(BROKER_TOPIC + SUBSCRIBE_URL) // application to topic (client need to subscribe topic first)
		public WebSocketMsg sendToAll(WebSocketMsg message) {
			return new WebSocketMsg(message.from(), message.text() + "_" + LocalDateTime.now());
		}

		@MessageMapping("/send-to-user") // user specific queue
		public void sendToUser(WebSocketMsg message, Principal principal) {
			String userName = principal.getName();
			log.info("current authenticated user: {}", userName);
			WebSocketMsg socketMsg = new WebSocketMsg(message.from(), message.text() + "_" + LocalDateTime.now());
			String destination = BROKER_QUEUE + SUBSCRIBE_URL;
			// destination will be converted to /user/{destination}, client have to subscribe this first.
			simpMessagingTemplate.convertAndSendToUser(userName, destination, socketMsg);
		}

		public record WebSocketMsg(String from, String text) {

		}

	}

}