package com.jiandong;

import com.jiandong.axon.CreateEntityCommand;
import com.jiandong.axon.TestController;
import com.jiandong.axon.TestEntity;
import com.jiandong.axon.TestSubEntity;
import com.jiandong.batch.*;
import com.jiandong.config.CustomHighestPriorityPropertiesListener;
import org.axonframework.springboot.autoconfig.AxonAutoConfiguration;
import org.axonframework.springboot.autoconfig.JdbcAutoConfiguration;
import org.axonframework.springboot.autoconfig.JpaAutoConfiguration;
import org.axonframework.springboot.autoconfig.JpaEventStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by mjd on 2021/5/14 13:23
 */
@EnableScheduling
@SpringBootApplication(exclude = {
        AxonAutoConfiguration.class, JpaAutoConfiguration.class, JdbcAutoConfiguration.class, JpaEventStoreAutoConfiguration.class
})
@ComponentScan(excludeFilters = {
        @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                CreateEntityCommand.class, TestController.class, TestEntity.class, TestSubEntity.class,
                JobController.class, CsvFileImportJob.class, CsvFileListener.class, CsvItemReader.class, CsvItemProcessor.class, CsvItemWriter.class
        })
})
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new CustomHighestPriorityPropertiesListener());
        application.run(args);
    }
}
