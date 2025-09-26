package com.jiandong.batch;

import java.util.concurrent.CountDownLatch;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.batch.autoconfigure.BatchAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcClientAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.JdbcTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest(classes = {CsvFileImportJob.class})
@ImportAutoConfiguration(classes = {
		DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
		BatchAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class, JdbcClientAutoConfiguration.class,
})
public class CsvFileImportJobTest {

	@Autowired @Qualifier("CsvJob") Job csvFileImportJob;

	@Autowired JobOperator jobOperator;

	@MockitoBean
	JobExecutionListener csvFileListener; // mock this bean - to know the lifecycle of the integration testing

	@Autowired JdbcClient jdbcClient; // for verifing job results

	@Test
	@Sql(scripts = "classpath:people.sql")
	void happyScenario() throws Exception {
		// Given
		CountDownLatch latch = new CountDownLatch(1);
		doAnswer(invocation -> {
			latch.countDown(); return null;
		}).when(csvFileListener).afterJob(any(JobExecution.class));

		JobParameters jobParameters = new JobParametersBuilder()
				.addString("csvFilePath", "src/test/resources/batch-data.csv")
				.toJobParameters();
		// When
		jobOperator.start(csvFileImportJob, jobParameters);
		latch.await(); // until listener finished
		// Then
		var personList = jdbcClient.sql("select * from people")
				.query(CsvFileImportJob.Person.class)
				.list();
		Assertions.assertThat(personList)
				.hasSize(5)
				.last()
				.extracting(person -> person.firstName())
				.isEqualTo("E");
	}

}
