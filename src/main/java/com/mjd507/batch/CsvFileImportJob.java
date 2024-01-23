package com.mjd507.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;

@Configuration
@RequiredArgsConstructor
public class CsvFileImportJob {
    private final CsvItemReader csvItemReader;
    private final CsvItemProcessor csvItemProcessor;
    private final CsvItemWriter csvItemWriter;
    private final CsvFileListener csvFileListener;
    private final PlatformTransactionManager transactionManager;

    @Bean(name = "CsvJob")
    public Job csvFileImportJob(JobRepository jobRepository) {
        return new JobBuilder("CsvFileJobBuilder", jobRepository)
                .preventRestart()
                .incrementer(new RunIdIncrementer())
                .start(csvFileImportStep(jobRepository))
                .listener(csvFileListener)
                .build();
    }


    @Bean
    public Step csvFileImportStep(JobRepository jobRepository) {
        return new StepBuilder("CsvFileStepBuilder", jobRepository)
                .<Person, Person>chunk(1, transactionManager)
                .reader(csvItemReader)
                .processor(csvItemProcessor)
                .writer(csvItemWriter)
                .transactionManager(transactionManager)
                .build();
    }
}
