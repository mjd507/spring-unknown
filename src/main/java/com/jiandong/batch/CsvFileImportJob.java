package com.jiandong.batch;

import java.lang.reflect.Field;
import java.util.Arrays;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CsvFileImportJob {

	private static final Logger log = LoggerFactory.getLogger(CsvFileImportJob.class);

	private final PlatformTransactionManager transactionManager;

	private final DataSource dataSource;

	public CsvFileImportJob(PlatformTransactionManager transactionManager, DataSource dataSource) {
		this.transactionManager = transactionManager;
		this.dataSource = dataSource;
	}

	@Bean(name = "CsvJob")
	public Job csvFileImportJob(JobRepository jobRepository, FlatFileItemReader<Employee> csvItemReader) {
		return new JobBuilder("CsvFileJobBuilder", jobRepository)
				.preventRestart()
				.start(new StepBuilder("CsvFileStepBuilder", jobRepository)
						.<Employee, Employee>chunk(1)
						.reader(csvItemReader)
						.processor(csvItemProcessor())
						.writer(csvItemWriter())
						.transactionManager(transactionManager)
						.build())
				.listener(csvFileListener())
				.build();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Employee> csvItemReader(@Value("#{jobParameters['csvFilePath']}") String csvFilePath) {
		FileSystemResource resource = new FileSystemResource(csvFilePath);
		return new FlatFileItemReaderBuilder<Employee>()
				.resource(resource)
				.delimited(config -> config
						.delimiter(",")
						.names(Arrays.stream(Employee.class.getDeclaredFields())
								.map(Field::getName).toArray(String[]::new))
				)
				.targetType(Employee.class)
				.linesToSkip(1)
				.strict(false)
				.skippedLinesCallback(s -> log.info("skip first line: {}", s))
				.saveState(false)
				.build();
	}

	@Bean
	public JobExecutionListener csvFileListener() {
		return new JobExecutionListener() {

			@Override
			public void beforeJob(JobExecution jobExecution) {
				log.info("before job ..., id:{}", jobExecution.getJobInstanceId());
			}

			@Override
			public void afterJob(JobExecution jobExecution) {
				StepExecution[] stepExecutions = jobExecution.getStepExecutions().toArray(new StepExecution[] {});
				log.info("after job ..., readCount: {}", stepExecutions[0].getReadCount());
			}
		};
	}

	@Bean
	ItemProcessor<Employee, Employee> csvItemProcessor() {
		return employee -> {
			String firstName = employee.firstName().toUpperCase();
			String lastName = employee.lastName().toUpperCase();

			Employee transformedEmployee = new Employee(firstName, lastName);

			log.info("Converting (" + employee + ") into (" + transformedEmployee + ")");

			return transformedEmployee;
		};
	}

	@Bean
	ItemWriter<Employee> csvItemWriter() {
		return new JdbcBatchItemWriterBuilder<Employee>()
				.dataSource(dataSource)
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO employee (first_name, last_name) VALUES (:firstName, :lastName)")
				.build();
	}

	public record Employee(String firstName, String lastName) {

	}

}
