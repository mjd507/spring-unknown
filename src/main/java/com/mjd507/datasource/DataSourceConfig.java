package com.mjd507.datasource;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;

@Profile("data-source")
@Configuration
@Slf4j
public class DataSourceConfig {

    @Bean
    public ApplicationRunner dataSourceRunner (DataSourceProperties dataSourceProperties) {
        return args -> {
            DataSource dataSource = createHikariDateSource(
                    dataSourceProperties.determineDriverClassName(),
                    dataSourceProperties.determineUrl(),
                    dataSourceProperties.determineUsername(),
                    dataSourceProperties.determinePassword()
            );
            log.info("dataSource: {}", dataSource);
            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute("SELECT CURRENT_DATE FROM DUAL");
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    Date date = resultSet.getDate(1);
                    log.info("date: {}", date);
                }
            }
        };
    }

//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSourceProperties h2DataSourceProperties() {
//        return new DataSourceProperties();
//    }
    
    private static DataSource createHikariDateSource(String driverClassName,
                                             String url, String user, String password) {
      return  DataSourceBuilder.create(ClassLoader.getSystemClassLoader())
                .type(HikariDataSource.class)
                .driverClassName(driverClassName)
                .url(url)
                .username(user)
                .password(password)
                .build();
    }
    
}
