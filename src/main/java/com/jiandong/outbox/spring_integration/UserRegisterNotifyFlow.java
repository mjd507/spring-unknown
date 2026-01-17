package com.jiandong.outbox.spring_integration;

import java.time.Duration;

import javax.sql.DataSource;

import com.jiandong.outbox.UserRegister;
import com.jiandong.support.SupportBean;
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
import org.springframework.integration.jdbc.store.channel.JsonChannelMessageStorePreparedStatementSetter;
import org.springframework.integration.jdbc.store.channel.JsonMessageRowMapper;
import org.springframework.integration.jdbc.store.channel.PostgresChannelMessageStoreQueryProvider;

@Configuration
@EnableIntegration
public class UserRegisterNotifyFlow {

	private static final Logger log = LoggerFactory.getLogger(UserRegisterNotifyFlow.class);

	private final SupportBean supportBean;

	public UserRegisterNotifyFlow(SupportBean supportBean) {
		this.supportBean = supportBean;
	}

	@Bean
	public DirectChannel registeredChannel() {
		return new DirectChannel();
	}

	@Bean
	JdbcChannelMessageStore jdbcChannelMessageStore(DataSource dataSource) {
		JdbcChannelMessageStore jdbcChannelMessageStore = new JdbcChannelMessageStore(dataSource);
		jdbcChannelMessageStore.setChannelMessageStoreQueryProvider(new PostgresChannelMessageStoreQueryProvider());
		// Enable Json Serialization
		jdbcChannelMessageStore.setPreparedStatementSetter(new JsonChannelMessageStorePreparedStatementSetter());
		String trustedPackageName = UserRegister.class.getPackageName();
		jdbcChannelMessageStore.setMessageRowMapper(new JsonMessageRowMapper(trustedPackageName));
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
			supportBean.reject(register);
			throw new RuntimeException("simulate sending error.");
		}
		log.info("sending email to users mailbox: {}", email);
		supportBean.ack(register);
	}

}
