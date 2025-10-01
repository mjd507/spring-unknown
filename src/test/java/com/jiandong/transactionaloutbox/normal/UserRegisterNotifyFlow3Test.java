package com.jiandong.transactionaloutbox.normal;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.jiandong.transactionaloutbox.UserRegister;
import com.jiandong.transactionaloutbox.UserRegisterReq;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootTest(classes = {
		UserRegisterNotifyFlow3.class, UserRegisterDao.class, OutboxEventDao.class,
		NotifyService.class, UserRegisterNotifyFlow3Test.UserRegisterCaller.class
})
@ImportAutoConfiguration(classes = {
		DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class, JdbcClientAutoConfiguration.class, TaskExecutionAutoConfiguration.class
})
@EnableTransactionManagement
@DirtiesContext
class UserRegisterNotifyFlow3Test {

	@Autowired UserRegisterCaller userRegisterCaller;

	@Autowired OutboxEventDao outboxEventDao;

	@Autowired UserRegisterDao userRegisterDao;

	@Autowired JdbcClient jdbcClient;

	@Sql(scripts = {
			"classpath:transactionaloutbox/user_register.sql",
			"classpath:transactionaloutbox/normal/outbox_event.sql",
	})
	@Test
	void registerSuccessNotifyFailed() throws InterruptedException {
		// GIVEN
		CountDownLatch latch = new CountDownLatch(1);
		var registerReq = new UserRegisterReq("jiandong-1", "jiandong-1@abc.com");
		// WHEN
		userRegisterCaller.callRegisterUser(registerReq);
		latch.await(1, TimeUnit.SECONDS); // need to ensure task executed
		// THEN
		UserRegister user = userRegisterDao.findUser("jiandong-1");
		Assertions.assertThat(user).isNotNull();
		List<OutboxEvent> outboxEvents = outboxEventDao.listNonCompletedEvents();
		Assertions.assertThat(outboxEvents)
				.hasSize(1);
	}

	@Sql(scripts = {
			"classpath:transactionaloutbox/user_register.sql",
			"classpath:transactionaloutbox/normal/outbox_event.sql",
	})
	@Test
	void registerSuccessNotifySuccess() throws InterruptedException {
		// GIVEN
		CountDownLatch latch = new CountDownLatch(1);
		var registerReq = new UserRegisterReq("jiandong-2", "jiandong-2@cn.com");
		// WHEN
		userRegisterCaller.callRegisterUser(registerReq);
		latch.await(1, TimeUnit.SECONDS); // need to ensure task executed
		// THEN
		UserRegister user = userRegisterDao.findUser("jiandong-2");
		Assertions.assertThat(user).isNotNull();
		List<OutboxEvent> outboxEvents = outboxEventDao.listNonCompletedEvents();
		Assertions.assertThat(outboxEvents).isEmpty();
		List<@Nullable OutboxEvent> events = jdbcClient.sql("select * from outbox_event")
				.query(OutboxEvent.class)
				.list();
		Assertions.assertThat(events).hasSize(1);
	}

	@Component
	static class UserRegisterCaller {

		private final UserRegisterNotifyFlow3 UserRegisterNotifyFlow3;

		UserRegisterCaller(UserRegisterNotifyFlow3 UserRegisterNotifyFlow3) {
			this.UserRegisterNotifyFlow3 = UserRegisterNotifyFlow3;
		}

		public void callRegisterUser(UserRegisterReq registerReq) {
			UserRegisterNotifyFlow3.register(registerReq);
		}

	}

}
