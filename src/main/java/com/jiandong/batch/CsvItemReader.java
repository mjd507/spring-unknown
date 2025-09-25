package com.jiandong.batch;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.RecordFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class CsvItemReader extends FlatFileItemReader<Person> {

	private final Logger log = LoggerFactory.getLogger(CsvItemReader.this.getClass());

	@Value("#{jobParameters['csvFilePath']}")
	private String filePath;

	@Override
	public void afterPropertiesSet() throws Exception {
		initializeReader();
		super.afterPropertiesSet();
	}

	private void initializeReader() {
		setResource(new FileSystemResource(filePath));
		setLineMapper(lineMapper());
		setLinesToSkip(1);
		setStrict(false);
		setSkippedLinesCallback(s -> log.info("skip first line: {}", s));
	}

	private LineMapper<Person> lineMapper() {
		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setDelimiter(",");
		delimitedLineTokenizer.setNames(Arrays.stream(Person.class.getDeclaredFields()).map(Field::getName).toArray(String[]::new));
		DefaultLineMapper<Person> defaultLineMapper = new DefaultLineMapper<>();
		defaultLineMapper.setLineTokenizer(delimitedLineTokenizer);

		RecordFieldSetMapper<Person> fieldSetMapper = new RecordFieldSetMapper<>(Person.class);
		defaultLineMapper.setFieldSetMapper(fieldSetMapper);
		return defaultLineMapper;
	}

}