package com.jiandong.testcontainer;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

/**
 * The base contract for JUnit tests based on the container for Postgres.
 * The Testcontainers 'reuse' option must be disabled, so, Ryuk container is started
 * and will clean all the containers up from this test suite after JVM exit.
 * Since the Postgres container instance is shared via static property, it is going to be
 * started only once per JVM; therefore, the target Docker container is reused automatically.
 *
 */
@Testcontainers(disabledWithoutDocker = true)
public interface PostgresContainerTest {

	String INIT_SCRIPTS_DIR = "docker/db/init";

	String CONTAINER_INIT_DIR = "/docker-entrypoint-initdb.d/";

	PostgreSQLContainer POSTGRES_CONTAINER = new PostgreSQLContainer("postgres:11")
			.withCopyToContainer(MountableFile.forClasspathResource(INIT_SCRIPTS_DIR), CONTAINER_INIT_DIR);

	@BeforeAll
	static void startContainer() {
		POSTGRES_CONTAINER.start();
	}

	static DataSource postgresqlDataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(POSTGRES_CONTAINER.getDriverClassName());
		dataSource.setJdbcUrl(POSTGRES_CONTAINER.getJdbcUrl());
		dataSource.setUsername(POSTGRES_CONTAINER.getUsername());
		dataSource.setPassword(POSTGRES_CONTAINER.getPassword());
		return dataSource;
	}

}
