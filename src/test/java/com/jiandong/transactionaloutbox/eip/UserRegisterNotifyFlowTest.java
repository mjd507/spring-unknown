package com.jiandong.transactionaloutbox.eip;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.jiandong.transactionaloutbox.UserRegister;
import com.jiandong.transactionaloutbox.UserRegisterDao;
import com.jiandong.transactionaloutbox.UserRegisterReq;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.integration.autoconfigure.IntegrationAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.jdbc.store.JdbcChannelMessageStore;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootTest(classes = {
		UserRegisterNotifyFlow.class, UserRegisterService1.class, UserRegisterDao.class
})
@ImportAutoConfiguration(classes = {
		IntegrationAutoConfiguration.class,
		DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class, JdbcClientAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
})
@EnableTransactionManagement
@DirtiesContext
class UserRegisterNotifyFlowTest {

	@Autowired UserRegisterService1 userRegisterService1;

	@Autowired UserRegisterDao userRegisterDao;

	@Autowired JdbcChannelMessageStore jdbcChannelMessageStore;

	@Sql(scripts = {"classpath:transactionaloutbox/user_register.sql"})
	@Test
	void registerSuccessNotifyFailed() throws InterruptedException {
		// GIVEN
		CountDownLatch latch = new CountDownLatch(1);
		var registerReq = new UserRegisterReq("jiandong-1", "jiandong-1@abc.com");
		// WHEN
		userRegisterService1.register(registerReq);
		latch.await(1, TimeUnit.SECONDS); // need to ensure task executed
		// THEN
		UserRegister user = userRegisterDao.findUser("jiandong-1");
		Assertions.assertThat(user).isNotNull();
		Message<?> message = jdbcChannelMessageStore.pollMessageFromGroup("registered-user");
		Assertions.assertThat(message)
				.isNotNull()
				.extracting(Message::getPayload)
				.isExactlyInstanceOf(UserRegister.class)
				.extracting("email")
				.isEqualTo("jiandong-1@abc.com");
	}

	@Sql(scripts = {"classpath:transactionaloutbox/user_register.sql"})
	@Test
	void registerSuccessNotifySuccess() throws InterruptedException {
		// GIVEN
		CountDownLatch latch = new CountDownLatch(1);
		var registerReq = new UserRegisterReq("jiandong-2", "jiandong-2@cn.com");
		// WHEN
		userRegisterService1.register(registerReq);
		latch.await(1, TimeUnit.SECONDS); // need to ensure task executed
		// THEN
		Message<?> message = jdbcChannelMessageStore.pollMessageFromGroup("registered-user");
		Assertions.assertThat(message).isNull();
	}

}
