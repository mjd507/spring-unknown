package com.jiandong.batch;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CsvFileImportJob {

	private final PlatformTransactionManager transactionManager;

	private final CsvFileListener csvFileListener;

	private final CsvItemReader csvItemReader;

	private final CsvItemProcessor csvItemProcessor;

	private final CsvItemWriter csvItemWriter;

	public CsvFileImportJob(PlatformTransactionManager transactionManager, CsvFileListener csvFileListener, CsvItemReader csvItemReader, CsvItemProcessor csvItemProcessor, CsvItemWriter csvItemWriter) {
		this.transactionManager = transactionManager;
		this.csvFileListener = csvFileListener;
		this.csvItemReader = csvItemReader;
		this.csvItemProcessor = csvItemProcessor;
		this.csvItemWriter = csvItemWriter;
	}

	@Bean(name = "CsvJob")
	public Job csvFileImportJob(JobRepository jobRepository) {
		return new JobBuilder("CsvFileJobBuilder", jobRepository)
				.preventRestart()
//				.incrementer(new RunIdIncrementer())
				.start(csvFileImportStep(jobRepository))
				.listener(csvFileListener)
				.build();
	}

	@Bean
	public Step csvFileImportStep(JobRepository jobRepository) {
		return new StepBuilder("CsvFileStepBuilder", jobRepository)
				.<Person, Person>chunk(1)
				.reader(csvItemReader)
				.processor(csvItemProcessor)
				.writer(csvItemWriter)
				.transactionManager(transactionManager)
				.build();
	}

}
