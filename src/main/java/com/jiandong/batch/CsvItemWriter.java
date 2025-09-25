package com.jiandong.batch;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.stereotype.Component;

@Component
public class CsvItemWriter extends JdbcBatchItemWriter<Person> {

	private final Logger log = LoggerFactory.getLogger(CsvItemWriter.this.getClass());

	private final DataSource dataSource;

	CsvItemWriter(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public void afterPropertiesSet() {
		initWriter();
		super.afterPropertiesSet();
	}

	private void initWriter() {
		this.setDataSource(dataSource);
		this.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		this.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
	}

}
