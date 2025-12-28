package com.jiandong.testcontainer;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.activemq.ActiveMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import org.springframework.boot.activemq.autoconfigure.ActiveMQConnectionDetails;

@Testcontainers(disabledWithoutDocker = true)
public interface ActivemqContainerTest {

	ActiveMQContainer ACTIVE_MQ_CONTAINER = new ActiveMQContainer(DockerImageName.parse("apache/activemq-classic:6.1.8"));

	@BeforeAll
	static void startContainer() {
		ACTIVE_MQ_CONTAINER.start();
	}

	static ActiveMQConnectionDetails connectionDetails() {

		return new ActiveMQConnectionDetails() {

			@Override
			public @NonNull String getBrokerUrl() {
				return ACTIVE_MQ_CONTAINER.getBrokerUrl();
			}

			@Override
			public @Nullable String getUser() {
				return ACTIVE_MQ_CONTAINER.getUser();
			}

			@Override
			public @Nullable String getPassword() {
				return ACTIVE_MQ_CONTAINER.getPassword();
			}
		};
	}

}
