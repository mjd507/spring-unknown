package com.jiandong.performance.transactions;

import com.jiandong.support.SupportBean;
import com.jiandong.testcontainer.PostgresContainerTest;
import com.jiandong.testcontainer.PostgresDataSourceConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.transaction.autoconfigure.TransactionAutoConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootTest(classes = {TransactionDbConn.class, SupportBean.class, TransactionDbConnTest.TransactionCaller.class})
@ImportAutoConfiguration(classes = {
		PostgresDataSourceConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class, JdbcClientAutoConfiguration.class,
		TransactionAutoConfiguration.class,
})
@EnableTransactionManagement
@DirtiesContext
class TransactionDbConnTest implements PostgresContainerTest {

	@Autowired TransactionCaller transactionCaller;

	/**
	 * haven't found a good way to test that.
	 * steps I used to verify:
	 * 1. add breakpoints to each line of this method.
	 * 2. add breakpoints to PlatformTransactionManager(getTransaction/commit)
	 * test in debug mode, will see that before method call, getTransaction will be called first,
	 * and after the long external call, commit will be called.
	 */
	@Test
	void case1() {
		Assertions.assertThatCode(() -> transactionCaller.callCase1())
				.doesNotThrowAnyException();
	}

	/**
	 * haven't found a good way to test that.
	 * steps I used to verify:
	 * 1. add breakpoints to each line of this method.
	 * 2. add breakpoints to PlatformTransactionManager(getTransaction/commit)
	 * test in debug mode, will see that only when executing line1, getTransaction will be called,
	 * and before the long external call, commit was called.
	 */
	@Test
	void fix1() {
		Assertions.assertThatCode(() -> transactionCaller.callFix1())
				.doesNotThrowAnyException();
	}

	/**
	 * haven't found a good way to test that.
	 * steps I used to verify:
	 * 1. add breakpoints to each line of this method.
	 * 2. add breakpoints to PlatformTransactionManager(getTransaction/commit)
	 * test in debug mode, will see that before method call, getTransaction will be called first,
	 * when going to inner new transaction, getTransaction will be called again.
	 * then the inner transaction commit will be called, last is the outer transaction commit.
	 */
	@Test
	void case2() {
		Assertions.assertThatCode(() -> transactionCaller.callCase2())
				.doesNotThrowAnyException();
	}

	/**
	 * haven't found a good way to test that.
	 * steps I used to verify:
	 * 1. add breakpoints to each line of this method.
	 * 2. add breakpoints to PlatformTransactionManager(getTransaction/commit)
	 * test in debug mode, will see that only when executing line1, 1st getTransaction will be called,
	 * and before the 2nd inner transaction call, 1st commit was called.
	 * then 2nd getTransaction, finally 2nd commit.
	 */
	@Test
	void fix2() {
		Assertions.assertThatCode(() -> transactionCaller.callFix2())
				.doesNotThrowAnyException();
	}

	@Component
	public static class TransactionCaller {

		final TransactionDbConn transactionDbConn;

		TransactionCaller(TransactionDbConn transactionDbConn) {
			this.transactionDbConn = transactionDbConn;
		}

		void callCase1() {
			transactionDbConn.case1();
		}

		void callFix1() {
			transactionDbConn.fix1();
		}

		void callCase2() {
			transactionDbConn.case2();
		}

		void callFix2() {
			transactionDbConn.fix2();
		}

	}

}
