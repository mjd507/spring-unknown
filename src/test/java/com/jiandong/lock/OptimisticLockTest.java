package com.jiandong.lock;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

import com.jiandong.testcontainer.PostgresContainerTest;
import com.jiandong.testcontainer.PostgresDataSourceConfiguration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.transaction.autoconfigure.TransactionAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootTest(classes = {OptimisticLock.class})
@ImportAutoConfiguration(classes = {
		PostgresDataSourceConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class, JdbcClientAutoConfiguration.class,
		TransactionAutoConfiguration.class,
})
@EnableTransactionManagement
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OptimisticLockTest implements PostgresContainerTest {

	@MockitoSpyBean
	private OptimisticLock optimisticLock;

	@Test
	@Order(1)
	public void UniqueConstraintFailure() throws InterruptedException {
		// GIVEN
		int threadCount = 3;
		// concurrent insertion
		CountDownLatch latch = new CountDownLatch(threadCount);
		for (int i = 0; i < threadCount; i++) {
			Thread.ofVirtual().start(() -> {
				OptimisticLock.Payment payment = new OptimisticLock.Payment(null, "pmt-01", BigDecimal.valueOf(100), null, null);
				// WHEN
				optimisticLock.savePayment(payment);
				latch.countDown();
			});
		}
		latch.await();
		// THEN
		Mockito.verify(optimisticLock, Mockito.times(threadCount)).savePayment(Mockito.any());
	}

	@Test
	@Order(2)
	public void OptimisticLockingFailure() throws InterruptedException {
		// GIVEN
		int threadCount = 3;
		CountDownLatch latch = new CountDownLatch(threadCount);
		for (int i = 0; i < threadCount; i++) {
			Thread.ofVirtual().start(() -> {
				OptimisticLock.Payment payment = new OptimisticLock.Payment(null, "pmt-01", BigDecimal.valueOf(100), null, null);
				// WHEN
				optimisticLock.savePayment(payment);
				latch.countDown();
			});
		}
		latch.await();
		// THEN
		Mockito.verify(optimisticLock, Mockito.times(threadCount)).savePayment(Mockito.any());
	}

}
