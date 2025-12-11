package com.jiandong.testcontainer;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostgresDataSourceConfiguration {

	@Bean
	DataSource postgresqlDataSource() {
		return PostgresContainerTest.postgresqlDataSource();
	}

}
