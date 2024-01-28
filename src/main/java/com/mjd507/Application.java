package com.mjd507;

import com.mjd507.axon.CreateEntityCommand;
import com.mjd507.axon.TestController;
import com.mjd507.axon.TestEntity;
import com.mjd507.axon.TestSubEntity;
import com.mjd507.batch.*;
import com.mjd507.config.CustomHighestPriorityPropertiesListener;
import com.mjd507.jms.JmsConfig;
import com.mjd507.jms.JmsTestController;
import com.mjd507.jms.MsgReceiver;
import com.mjd507.jms.MsgSender;
import org.axonframework.springboot.autoconfig.AxonAutoConfiguration;
import org.axonframework.springboot.autoconfig.JdbcAutoConfiguration;
import org.axonframework.springboot.autoconfig.JpaAutoConfiguration;
import org.axonframework.springboot.autoconfig.JpaEventStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by mjd on 2021/5/14 13:23
 */
@EnableScheduling
@SpringBootApplication(exclude = {
        AxonAutoConfiguration.class, JpaAutoConfiguration.class, JdbcAutoConfiguration.class, JpaEventStoreAutoConfiguration.class,
        JmsAutoConfiguration.class, ActiveMQAutoConfiguration.class, ArtemisAutoConfiguration.class
})
@ComponentScan(excludeFilters = {
        @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                CreateEntityCommand.class, TestController.class, TestEntity.class, TestSubEntity.class,
                JobController.class, CsvFileImportJob.class, CsvFileListener.class, CsvItemReader.class, CsvItemProcessor.class, CsvItemWriter.class,
                JmsConfig.class, JmsTestController.class, MsgSender.class, MsgReceiver.class,
        })
})
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new CustomHighestPriorityPropertiesListener());
        application.run(args);
    }
}
