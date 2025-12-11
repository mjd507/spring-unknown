package com.jiandong.lock;

import com.jiandong.testcontainer.PostgresContainerTest;
import com.jiandong.testcontainer.PostgresDataSourceConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

// for usage test,
// see EventSchedulerTest.testScheduler
// and OutboxEventSchedulerTest.testScheduler
@SpringBootTest(classes = {ShedLockConfig.class, PostgresDataSourceConfiguration.class})
class ShedLockConfigTest implements PostgresContainerTest {

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
