package com.jiandong.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class CsvFileListener implements JobExecutionListener {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("before job ..., id:{}", jobExecution.getJobId());
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