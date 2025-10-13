package com.jiandong.lock;

import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

// for usage test,
// see EventSchedulerTest.testScheduler
// and OutboxEventSchedulerTest.testScheduler
@SpringBootTest(classes = {ShedLockConfig.class, DataSourceAutoConfiguration.class})
class ShedLockConfigTest {

	@Autowired
	ShedLockConfig shedLockConfig;

	@Autowired
	LockProvider jdbcLockProvider;

	@Test
	void testConfig() {
		assertThat(shedLockConfig).isNotNull();
		assertThat(jdbcLockProvider).isNotNull();
	}

}
