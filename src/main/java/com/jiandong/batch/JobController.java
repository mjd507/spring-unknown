package com.jiandong.batch;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private final Job CsvJob;

	private final JobLauncher jobLauncher;

	public JobController(Job csvJob, JobLauncher jobLauncher) {
		CsvJob = csvJob;
		this.jobLauncher = jobLauncher;
	}

	@GetMapping("job/run")
	public void run() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
		JobParameters jobParameters = new JobParametersBuilder()
				.addString("traceId", UUID.randomUUID().toString())
				.toJobParameters();
		jobLauncher.run(CsvJob, jobParameters);
	}

	@GetMapping("download")
	public Resource download() throws MalformedURLException {
		String path = "https://ts1.cn.mm.bing.net/th?id=OSK.9de2b20c1031e47165cb3e5aa4e40358&w=120&h=120&c=12&o=6&pid=SANGAM";
		Resource resource = new FileUrlResource(new URL(path));
		log.info(resource.getFilename());
		return resource;
	}

}
