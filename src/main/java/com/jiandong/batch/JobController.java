package com.jiandong.batch;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class JobController{
    private final Job CsvJob;
    private final JobLauncher jobLauncher;

    private final ApplicationContext applicationContext;
    @SneakyThrows
    @GetMapping("job/run")
    public void run() {
//        Job job = (Job) applicationContext.getBean("CsvJob");
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("traceId", UUID.randomUUID().toString())
                .toJobParameters();
        jobLauncher.run(CsvJob, jobParameters);
    }

    @SneakyThrows
    @GetMapping("download")
    public Resource download() {
        String path = "https://ts1.cn.mm.bing.net/th?id=OSK.9de2b20c1031e47165cb3e5aa4e40358&w=120&h=120&c=12&o=6&pid=SANGAM";
        Resource resource = new FileUrlResource(new URL(path));
        System.out.println(resource.getFilename());
        return resource;
    }

}
