package com.jiandong.lock;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OptimisticLock {

	private static final Logger log = LoggerFactory.getLogger(OptimisticLock.class);

	final String SELECT_PAYMENT = "select * from payment where pmt_id = :pmtId";

	final String INSERT_PAYMENT = "insert into payment (pmt_id, amount, version) "
			+ "VALUES (:pmtId, :amount, 1)";

	final String UPDATE_PAYMENT = "update payment set amount = :amount, version = version + 1 "
			+ "where pmt_id = :pmtId and version = :version";

	private final JdbcClient jdbcClient;

	public OptimisticLock(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	@Transactional
	public void savePayment(Payment payment) {
		Payment pmt = jdbcClient.sql(SELECT_PAYMENT)
				.param("pmtId", payment.pmtId)
				.query(Payment.class)
				.optional().orElse(null);

		if (pmt == null) {
			try {
				jdbcClient.sql(INSERT_PAYMENT)
						.param("pmtId", payment.pmtId)
						.param("amount", payment.amount)
						.update();
				log.info("payment insert success. pmtId={}", payment.pmtId);
			}
			catch (DuplicateKeyException duplicateKeyException) {
				log.error("duplicate insertion, unique constraint violation, payment id={}", payment.pmtId);
				// throw duplicateKeyException;
			}
		}
		else {
			int newVersion = pmt.version + 1;
			int cnt = jdbcClient.sql(UPDATE_PAYMENT)
					.param("pmtId", payment.pmtId)
					.param("amount", payment.amount)
					.param("version", pmt.version)
					.update();
			if (cnt > 0) {
				log.info("payment update success. pmtId={} , new version:{}", payment.pmtId, newVersion);
			}
			else {
				// in spring data jpa, usually throws ObjectOptimisticLockingFailureException
				log.error("payment update fail, optimistic lock exception: version {}", pmt.version);
			}
		}

	}

	public record Payment(
			Long id,
			String pmtId,
			BigDecimal amount,
			LocalDateTime createdAt,
			Integer version
	) {

	}

}
