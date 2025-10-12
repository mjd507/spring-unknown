package com.jiandong.performance.transactions;

import com.jiandong.support.SupportBean;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * when we start a transaction, it requires a database connection.
 * however, not all of us are care about the connection lifecycle (acquire/release),
 * when application get complex, it can easily cause performance issues.
 * </p>
 * scenario 1: long external call blocks the transaction commit.
 * scenario 2: inner new transaction blocks the outer transaction commit.
 */
@Service
public class TransactionDbConn {

	private final SupportBean supportBean;

	private final JdbcClient jdbcClient;

	private final TransactionTemplate transactionTemplate;

	public TransactionDbConn(SupportBean supportBean, JdbcClient jdbcClient, TransactionTemplate transactionTemplate) {
		this.supportBean = supportBean;
		this.jdbcClient = jdbcClient;
		this.transactionTemplate = transactionTemplate;
	}

	@Transactional
	public void case1() {
		jdbcClient.sql("select 1").query(Integer.class).single();
		supportBean.slowMethod(); // block transaction commit.
	}

	public void fix1() {
		transactionTemplate.execute(status -> jdbcClient.sql("select 1").query(Integer.class).single());
		supportBean.slowMethod();
	}

	@Transactional
	public void case2() {
		jdbcClient.sql("select 1").query(Integer.class).single();
		supportBean.innerLongTransaction(); // block outer transaction
	}

	public void fix2() {
		transactionTemplate.execute(status -> jdbcClient.sql("select 1").query(Integer.class).single());
		supportBean.innerLongTransaction();
	}

}
