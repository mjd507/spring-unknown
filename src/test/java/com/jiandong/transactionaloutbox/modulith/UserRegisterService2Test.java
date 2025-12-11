package com.jiandong.transactionaloutbox.modulith;

import java.util.List;
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
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.events.config.EnablePersistentDomainEvents;
import org.springframework.modulith.events.core.TargetEventPublication;
import org.springframework.modulith.events.jdbc.SpringModulithAutoImportAdapter;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static org.mockito.Mockito.doAnswer;

@SpringBootTest(classes = {
		UserRegisterService2.class, NotifyService2.class,
		UserRegisterDao.class, UserRegisterService2Test.UserRegisterCaller.class,
		EventPublicationService.class
})
@ImportAutoConfiguration(classes = {
		PostgresDataSourceConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class, JdbcClientAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
		SpringModulithAutoImportAdapter.class
})
@EnablePersistentDomainEvents
@EnableTransactionManagement
@DirtiesContext
class UserRegisterService2Test implements PostgresContainerTest {

	@Autowired UserRegisterService2Test.UserRegisterCaller userRegisterCaller;

	@Autowired UserRegisterDao userRegisterDao;

	@Autowired EventPublicationService eventPublicationService;

	@MockitoBean SupportBean supportBean;

	@Test
	void registerSuccessNotifyFailed() throws InterruptedException {
		// GIVEN
		CountDownLatch latch = new CountDownLatch(1);
		doAnswer(invocation -> {
			latch.countDown();
			return null;
		}).when(supportBean).reject(Mockito.any());
		var registerReq = new UserRegisterReq("jiandong-3", "jiandong-3@abc.com");
		// WHEN
		userRegisterCaller.callRegisterUser(registerReq);
		latch.await();
		// THEN
		UserRegister user = userRegisterDao.findUser("jiandong-3");
		Assertions.assertThat(user).isNotNull();
		List<TargetEventPublication> inCompleteEvents = eventPublicationService.listInCompleteEvents();
		Assertions.assertThat(inCompleteEvents)
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
		var registerReq = new UserRegisterReq("jiandong-4", "jiandong-4@cn.com");
		// WHEN
		userRegisterCaller.callRegisterUser(registerReq);
		latch.await();
		// THEN
		UserRegister user = userRegisterDao.findUser("jiandong-4");
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
