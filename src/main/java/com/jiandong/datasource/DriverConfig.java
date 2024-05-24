package com.jiandong.datasource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;

@Configuration
@Slf4j
@Profile("driver")
public class DriverConfig {

    @Bean
    public ApplicationRunner driverRunner() {
        return args -> {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            drivers.asIterator().forEachRemaining(driver -> {
                try {
                    log.info("driver: {}", driver.toString());
                    Properties properties = new Properties();
                    properties.put("user", "root");
                    properties.put("password", "root");

                    try (Connection connection = driver.connect("jdbc:h2:mem:testdb", properties);
                         Statement statement = connection.createStatement()) {
                        statement.execute("SELECT CURRENT_DATE FROM DUAL");
                        ResultSet resultSet = statement.getResultSet();
                        while (resultSet.next()) {
                            Date date = resultSet.getDate(1);
                            log.info("date: {}", date);
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        };

    }

}
