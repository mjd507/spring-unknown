package com.jiandong.transactionaloutbox.normal;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.jiandong.support.SupportBean;
import com.jiandong.testcontainer.PostgresContainerTest;
import com.jiandong.testcontainer.PostgresDataSourceConfiguration;
import com.jiandong.transactionaloutbox.UserRegister;
import com.jiandong.transactionaloutbox.UserRegisterDao;
import com.jiandong.transactionaloutbox.UserRegisterReq;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.mockito.Mockito.doAnswer;

@SpringBootTest(classes = {
		UserRegisterService3.class, UserRegisterDao.class, OutboxEventDao.class,
		NotifyService3.class, UserRegisterService3Test.UserRegisterCaller.class
})
@ImportAutoConfiguration(classes = {
		PostgresDataSourceConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class, JdbcClientAutoConfiguration.class, TaskExecutionAutoConfiguration.class
})
@EnableTransactionManagement
@DirtiesContext
class UserRegisterService3Test implements PostgresContainerTest {

	@Autowired UserRegisterCaller userRegisterCaller;

	@Autowired OutboxEventDao outboxEventDao;

	@Autowired UserRegisterDao userRegisterDao;

	@Autowired JdbcClient jdbcClient;

	@MockitoBean SupportBean supportBean;

	@Test
	void registerSuccessNotifyFailed() throws InterruptedException {
		// GIVEN
		CountDownLatch latch = new CountDownLatch(1);
		doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(supportBean).reject(Mockito.any());
		var registerReq = new UserRegisterReq("jiandong-5", "jiandong-5@abc.com");
		// WHEN
		userRegisterCaller.callRegisterUser(registerReq);
		latch.await();
		// THEN
		UserRegister user = userRegisterDao.findUser("jiandong-5");
		Assertions.assertThat(user).isNotNull();
		List<OutboxEvent> outboxEvents = outboxEventDao.listNonCompletedEvents(null);
		Assertions.assertThat(outboxEvents)
				.hasSize(1);
	}

	@Test
	void registerSuccessNotifySuccess() throws InterruptedException {
		// GIVEN
		CountDownLatch latch = new CountDownLatch(1);
		doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(supportBean).ack(Mockito.any());
		var registerReq = new UserRegisterReq("jiandong-6", "jiandong-6@cn.com");
		// WHEN
		userRegisterCaller.callRegisterUser(registerReq);
		latch.await();
		// THEN
		UserRegister user = userRegisterDao.findUser("jiandong-6");
		Assertions.assertThat(user).isNotNull();
		List<OutboxEvent> outboxEvents = outboxEventDao.listNonCompletedEvents(null);
		Assertions.assertThat(outboxEvents).isEmpty();
		List<@Nullable OutboxEvent> events = jdbcClient.sql("select * from outbox_event")
				.query(OutboxEvent.class)
				.list();
		Assertions.assertThat(events).hasSize(1);
	}

	@Component
	static class UserRegisterCaller {

		private final UserRegisterService3 UserRegisterService3;

		UserRegisterCaller(UserRegisterService3 UserRegisterService3) {
			this.UserRegisterService3 = UserRegisterService3;
		}

		public void callRegisterUser(UserRegisterReq registerReq) {
			UserRegisterService3.register(registerReq);
		}

	}

}
