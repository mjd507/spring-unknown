package com.jiandong.transactionaloutbox.eip;

import java.time.Duration;

import javax.sql.DataSource;

import com.jiandong.transactionaloutbox.UserRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.jdbc.store.JdbcChannelMessageStore;
import org.springframework.integration.jdbc.store.channel.H2ChannelMessageStoreQueryProvider;

@Configuration
@EnableIntegration
public class UserRegisterNotifyFlow {

	private static final Logger log = LoggerFactory.getLogger(UserRegisterNotifyFlow.class);

	@Bean
	public DirectChannel registeredChannel() {
		return new DirectChannel();
	}

	@Bean
	JdbcChannelMessageStore jdbcChannelMessageStore(DataSource dataSource) {
		JdbcChannelMessageStore jdbcChannelMessageStore = new JdbcChannelMessageStore(dataSource);
		jdbcChannelMessageStore.setChannelMessageStoreQueryProvider(new H2ChannelMessageStoreQueryProvider());
		return jdbcChannelMessageStore;
	}

	@Bean
	public QueueChannel registeredStore(JdbcChannelMessageStore jdbcChannelMessageStore) {
		return MessageChannels.queue(jdbcChannelMessageStore, "registered-user").getObject();
	}

	@Bean
	public IntegrationFlow registerNotifyFlow(JdbcChannelMessageStore jdbcChannelMessageStore) {
		return IntegrationFlow.from(registeredChannel())
				.channel(registeredStore(jdbcChannelMessageStore)) // put into message store
				.handle(message -> {
					this.notify(((UserRegister) message.getPayload()));
				}, e -> e
						.poller(Pollers.fixedDelay(Duration.ofMinutes(5))
								.transactional()))
				.get();
	}

	private void notify(UserRegister register) {
		String email = register.email();
		// put in last step in case of duplicate sending
		if (email.contains("@abc.com")) {
			throw new RuntimeException("simulate sending error.");
		}
		log.info("sending email to users mailbox: {}", email);
	}

}
