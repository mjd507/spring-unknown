package com.jiandong.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CsvFileListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("before job ...");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        StepExecution[] stepExecutions = jobExecution.getStepExecutions().toArray(new StepExecution[]{});
        StepExecution stepExecution = stepExecutions[0];
        long readCount = stepExecution.getReadCount();
        System.out.println("read count:" + readCount);
        log.info("after job ...");
    }
}
