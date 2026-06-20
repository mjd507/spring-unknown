package com.jiandong;

import com.jiandong.testcontainer.ActivemqConnectionConfiguration;
import com.jiandong.testcontainer.ActivemqContainerTest;
import com.jiandong.testcontainer.PostgresContainerTest;
import com.jiandong.testcontainer.PostgresDataSourceConfiguration;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "spring.profiles.active=test")
@Testcontainers(disabledWithoutDocker = true)
@Import(value = {PostgresDataSourceConfiguration.class, ActivemqConnectionConfiguration.class})
@DirtiesContext
class ApplicationTest implements PostgresContainerTest, ActivemqContainerTest {

	@Test
	void contextLoad() {

	}

}
