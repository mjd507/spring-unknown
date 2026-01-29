package com.jiandong.websocket;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.jiandong.support.JwtTokenHelper;
import com.jiandong.support.TestSecurityConfig;
import com.jiandong.websocket.WebSocketConfig.WebSocketController.WebSocketMsg;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.tomcat.autoconfigure.servlet.TomcatServletWebServerAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
		classes = {WebSocketConfig.class, TestSecurityConfig.class},
		webEnvironment = WebEnvironment.DEFINED_PORT // do not use default mock, which won't start a tomcat web server
)
@TestPropertySource(properties = {"server.port=9999"})
@ImportAutoConfiguration({TomcatServletWebServerAutoConfiguration.class, DispatcherServletAutoConfiguration.class})
@DirtiesContext
class WebSocketConfigTest {

	private static final Logger log = LoggerFactory.getLogger(WebSocketConfigTest.class);

	private static final int port = 9999;

	@Autowired JwtTokenHelper jwtTokenHelper;

	@Test
	void testSendToAll() throws Exception {
		CountDownLatch latch = new CountDownLatch(2);
		final AtomicReference<Throwable> failure = new AtomicReference<>();
		// GIVEN
		var stompSession1 = _createStompSession(jwtTokenHelper.generateNormalUserToken());
		var stompSession2 = _createStompSession(jwtTokenHelper.generateAdminUserToken());
		Consumer<WebSocketMsg> webSocketMsgConsumer = (webSocketMsg) -> {
			// Async THEN
			try {
				assertNotNull(webSocketMsg);
				assertEquals("topic", webSocketMsg.from());
				assertTrue(webSocketMsg.text().startsWith("Hello All_"));
			}
			catch (Throwable t) {
				failure.set(t);
			}
			finally {
				latch.countDown();
			}
		};
		stompSession1.subscribe("/topic/messages", new StompSubscribeHandler(webSocketMsgConsumer));
		stompSession2.subscribe("/topic/messages", new StompSubscribeHandler(webSocketMsgConsumer));
		// WHEN
		stompSession1.send("/app/send-to-all", new WebSocketMsg("topic", "Hello All"));
		latch.await();
		// Finally
		stompSession1.disconnect();
		stompSession2.disconnect();
		if (failure.get() != null) {
			throw new AssertionError(failure.get());
		}
	}

	@Test
	public void testSendToUser() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();
		final AtomicBoolean session1NeverBeCalled = new AtomicBoolean(true);
		// GIVEN
		var stompSession1 = _createStompSession(jwtTokenHelper.generateNormalUserToken());
		var stompSession2 = _createStompSession(jwtTokenHelper.generateAdminUserToken());
		stompSession1.subscribe("/user/queue/messages", new StompSubscribeHandler((webSocketMsg) -> {
			// Async THEN
			session1NeverBeCalled.set(false); // never be called
		}));
		stompSession2.subscribe("/user/queue/messages", new StompSubscribeHandler((webSocketMsg) -> {
			// Async THEN
			try {
				assertNotNull(webSocketMsg);
				assertEquals("queue", webSocketMsg.from());
				assertTrue(webSocketMsg.text().startsWith("I am session 2_"));
			}
			catch (Throwable t) {
				failure.set(t);
			}
			finally {
				latch.countDown();
			}
		}));
		// WHEN
		stompSession2.send("/app/send-to-user", new WebSocketMsg("queue", "I am session 2"));
		latch.await();
		// Finally
		assertThat(session1NeverBeCalled).isTrue();
		stompSession1.disconnect();
		stompSession2.disconnect();
		if (failure.get() != null) {
			throw new AssertionError(failure.get());
		}
	}

	private StompSession _createStompSession(String basicToken) throws Exception {

		WebSocketTransport websocketTransport = new WebSocketTransport(new StandardWebSocketClient());

		RestTemplate restTemplate = new RestTemplate();
		ClientHttpRequestInterceptor requestInterceptor = (request, body, execution) -> {
			request.getHeaders().add(HttpHeaders.AUTHORIZATION, basicToken);
			return execution.execute(request, body);
		};
		restTemplate.setInterceptors(List.of(requestInterceptor));

		RestTemplateXhrTransport xhrTransport = new RestTemplateXhrTransport(restTemplate);

		WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(List.of(xhrTransport, websocketTransport)));

		stompClient.setMessageConverter(new JacksonJsonMessageConverter());

		return stompClient.connectAsync(
						"http://localhost:" + port + "/websocket/connect",
						new StompSessionHandlerAdapter() {

						})
				.get();
	}

	record StompSubscribeHandler(Consumer<WebSocketMsg> consumer) implements StompFrameHandler {

		@Override
		public @NonNull Type getPayloadType(@NonNull StompHeaders headers) {
			return WebSocketMsg.class;
		}

		@Override
		public void handleFrame(@NonNull StompHeaders headers, Object payload) {
			consumer.accept((WebSocketMsg) payload);
		}

	}

}
