package com.mjd507.event;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@EnableAsync
@Configuration
@RequiredArgsConstructor
public class EventConfig {


    @Bean
    public ApplicationListener<ApplicationReadyEvent> publishEvent(ApplicationEventPublisher eventPublisher) {
        return event -> {
            log.info("start publishing event");
            eventPublisher.publishEvent(new MyEvent("hello, 2024"));
           log.info("initiate others");
        };
    }

    @TransactionalEventListener
    @Async
    public void MyLiser(MyEvent event) throws InterruptedException {
       log.info("received customize event: " + event.toString());
       log.info("handling customize event msg");
        Thread.sleep(5_000);
    }

    private static class MyEvent extends ApplicationEvent {
        public MyEvent(Object source) {
            super(source);
        }
    }


    @EventListener
    public void listener(ApplicationReadyEvent event) {
       log.info("take 3 " + event.toString());
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> readyEventApplicationListener() {
        return event ->log.info("take 2 " + event.toString());
    }

    @Bean
    public MyListener myListener() {
        return new MyListener();
    }

    public static class MyListener implements ApplicationListener<ApplicationReadyEvent> {

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
           log.info("take 1 " + event.toString());
        }
    }
}
