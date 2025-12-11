package com.jiandong.transactionaloutbox.eip;

import java.util.concurrent.CountDownLatch;

import com.jiandong.support.SupportBean;
import com.jiandong.testcontainer.PostgresContainerTest;
import com.jiandong.testcontainer.PostgresDataSourceConfiguration;
import com.jiandong.transactionaloutbox.UserRegister;
import com.jiandong.transactionaloutbox.UserRegisterDao;
import com.jiandong.transactionaloutbox.UserRegisterReq;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.integration.autoconfigure.IntegrationAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.jdbc.store.JdbcChannelMessageStore;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.mockito.Mockito.doAnswer;

@SpringBootTest(classes = {
		UserRegisterNotifyFlow.class, UserRegisterService1.class, UserRegisterDao.class
})
@ImportAutoConfiguration(classes = {
		IntegrationAutoConfiguration.class,
		PostgresDataSourceConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class, JdbcClientAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
})
@EnableTransactionManagement
@DirtiesContext
class UserRegisterNotifyFlowTest implements PostgresContainerTest {

	@Autowired UserRegisterService1 userRegisterService1;

	@Autowired UserRegisterDao userRegisterDao;

	@Autowired JdbcChannelMessageStore jdbcChannelMessageStore;

	@MockitoBean SupportBean supportBean;

	@Test
	void registerSuccessNotifyFailed() throws InterruptedException {
		// GIVEN
		CountDownLatch latch = new CountDownLatch(1);
		var registerReq = new UserRegisterReq("jiandong-1", "jiandong-1@abc.com");
		doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(supportBean).reject(Mockito.any());
		// WHEN
		userRegisterService1.register(registerReq);
		latch.await();
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

	@Test
	void registerSuccessNotifySuccess() throws InterruptedException {
		// GIVEN
		CountDownLatch latch = new CountDownLatch(1);
		var registerReq = new UserRegisterReq("jiandong-2", "jiandong-2@cn.com");
		doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(supportBean).ack(Mockito.any());
		// WHEN
		userRegisterService1.register(registerReq);
		latch.await();
		// THEN
		Message<?> message = jdbcChannelMessageStore.pollMessageFromGroup("registered-user");
		Assertions.assertThat(message).isNull();
	}

}
