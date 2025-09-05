package com.jiandong.batch;

import java.lang.reflect.Field;
import java.util.Arrays;

import javax.sql.DataSource;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CsvFileImportJob {

	private final DataSource dataSource;

	private final PlatformTransactionManager transactionManager;

	public CsvFileImportJob(DataSource dataSource, PlatformTransactionManager transactionManager) {
		this.dataSource = dataSource;
		this.transactionManager = transactionManager;
	}

	@Bean(name = "CsvJob")
	public Job csvFileImportJob(JobRepository jobRepository) {
		return new JobBuilder("CsvFileJobBuilder", jobRepository)
				.preventRestart()
				.incrementer(new RunIdIncrementer())
				.start(csvFileImportStep(jobRepository))
				.listener(csvFileListener())
				.build();
	}

	@Bean
	public JobExecutionListener csvFileListener() {
		return new CsvFileListener();
	}

	class CsvFileListener implements JobExecutionListener {

		private final Logger log = LoggerFactory.getLogger(CsvFileImportJob.this.getClass());

		@Override
		public void beforeJob(JobExecution jobExecution) {
			log.info("before job ...");
		}

		@Override
		public void afterJob(JobExecution jobExecution) {
			StepExecution[] stepExecutions = jobExecution.getStepExecutions().toArray(new StepExecution[] {});
			StepExecution stepExecution = stepExecutions[0];
			long readCount = stepExecution.getReadCount();
			System.out.println("read count:" + readCount);
			log.info("after job ...");
		}

	}

	@Bean
	public Step csvFileImportStep(JobRepository jobRepository) {
		return new StepBuilder("CsvFileStepBuilder", jobRepository)
				.<Person, Person>chunk(1, transactionManager)
				.reader(csvItemReader())
				.processor(csvItemProcessor())
				.writer(csvItemWriter())
				.transactionManager(transactionManager)
				.build();
	}

	@Bean
	public FlatFileItemReader<Person> csvItemReader() {
		return new CsvItemReader();
	}

	@Bean
	public ItemProcessor<Person, Person> csvItemProcessor() {
		return new CsvItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Person> csvItemWriter() {
		return new CsvItemWriter(dataSource);
	}

	static class CsvItemReader extends FlatFileItemReader<Person> {

		private final Logger log = LoggerFactory.getLogger(CsvItemReader.this.getClass());

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

	static class CsvItemProcessor implements ItemProcessor<Person, Person> {

		private final Logger log = LoggerFactory.getLogger(CsvItemProcessor.this.getClass());

		@Override
		public Person process(final Person person) {
			String firstName = person.firstName().toUpperCase();
			String lastName = person.lastName().toUpperCase();

			Person transformedPerson = new Person(firstName, lastName);

			log.info("Converting (" + person + ") into (" + transformedPerson + ")");

			return transformedPerson;
		}

	}

	static class CsvItemWriter extends JdbcBatchItemWriter<Person> {

		private final Logger log = LoggerFactory.getLogger(CsvItemWriter.this.getClass());

		private final DataSource dataSource;

		CsvItemWriter(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		@PostConstruct
		public void initWriter() {
			this.setDataSource(dataSource);
			this.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
			this.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
		}

	}

	public record Person(String firstName, String lastName) {

	}

}
