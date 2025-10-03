package com.jiandong.transactionaloutbox.modulith;

import java.util.List;
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
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.events.config.EnablePersistentDomainEvents;
import org.springframework.modulith.events.core.TargetEventPublication;
import org.springframework.modulith.events.jdbc.SpringModulithAutoImportAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootTest(classes = {
		UserRegisterService2.class, NotifyService2.class,
		UserRegisterDao.class, UserRegisterService2Test.UserRegisterCaller.class,
		EventPublicationService.class
})
@ImportAutoConfiguration(classes = {
		DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class, JdbcClientAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
		SpringModulithAutoImportAdapter.class
})
@EnablePersistentDomainEvents
@EnableTransactionManagement
@EnableAsync
@DirtiesContext
class UserRegisterService2Test {

	@Autowired UserRegisterService2Test.UserRegisterCaller userRegisterCaller;

	@Autowired UserRegisterDao userRegisterDao;

	@Autowired EventPublicationService eventPublicationService;

	@Sql(scripts = {"classpath:transactionaloutbox/user_register.sql"})
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
		List<TargetEventPublication> inCompleteEvents = eventPublicationService.listInCompleteEvents();
		Assertions.assertThat(inCompleteEvents)
				.hasSize(1);
	}

	@Sql(scripts = {"classpath:transactionaloutbox/user_register.sql"})
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

		List<TargetEventPublication> completeEvents = eventPublicationService.listCompleteEvents();
		Assertions.assertThat(completeEvents).hasSize(1);
	}

	@Component
	static class UserRegisterCaller {

		private final UserRegisterService2 userRegisterService2;

		UserRegisterCaller(UserRegisterService2 userRegisterService2) {
			this.userRegisterService2 = userRegisterService2;
		}

		public void callRegisterUser(UserRegisterReq registerReq) {
			userRegisterService2.register(registerReq);
		}

	}

}
