package com.mjd507.batch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
@Slf4j
public class CsvItemWriter extends JdbcBatchItemWriter<Person> {

    private final DataSource dataSource;

    @PostConstruct
    public void initWriter() {
        this.setDataSource(dataSource);
        this.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        this.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
    }

}
