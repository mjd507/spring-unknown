package com.jiandong.batch;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
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
import org.springframework.test.context.jdbc.Sql;

import static com.jiandong.support.SupportUtils.threadSleep;

@SpringBootTest(classes = {CsvFileImportJob.class})
@ImportAutoConfiguration(classes = {
		DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
		BatchAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class, JdbcClientAutoConfiguration.class,
})
class CsvFileImportJobTest {

	@Autowired @Qualifier("CsvJob") Job csvFileImportJob;

	@Autowired JobOperator jobOperator;

	@Autowired JdbcClient jdbcClient; // for verifying job results

	@Test
	@Sql(scripts = "classpath:batch/employee.sql")
	void happyScenario() throws Exception {
		// Given
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("csvFilePath", "src/test/resources/batch/batch-data.csv")
				.toJobParameters();
		// When
		JobExecution jobExecution = jobOperator.start(csvFileImportJob, jobParameters);
		// wait to be completed
		while (jobExecution.getStatus() != BatchStatus.COMPLETED) {
			threadSleep(500);
		}
		// Then
		var personList = jdbcClient.sql("select * from employee")
				.query(CsvFileImportJob.Employee.class)
				.list();
		Assertions.assertThat(personList)
				.hasSize(5)
				.last()
				.extracting(employee -> employee.firstName())
				.isEqualTo("E");
	}

}
