package com.jiandong.batch;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Arrays;

@Component
@Slf4j
public class CsvItemReader extends FlatFileItemReader<Person> {

    @SneakyThrows
    @PostConstruct
    public void initializeReader() {
        String source = "src/main/resources/sample-data1.csv";
        setResource(new FileSystemResource(source));
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

        BeanWrapperFieldSetMapper<Person> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Person.class);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }
}
