package com.jiandong.proxy;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Configuration
@Profile("spring-proxy")
public class SpringProxy {

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Documented
    @Reflective
    public @interface MyTransactional {
    }

    static boolean transactional(Object obj) {
        AtomicBoolean hasMyTransaction = new AtomicBoolean(false);
        List<Class<?>> classes = new ArrayList<>();
        classes.add(obj.getClass());
        Collections.addAll(classes, obj.getClass().getInterfaces());
        classes.forEach(clz -> {
            ReflectionUtils.doWithMethods(clz, method -> {
                if (method.getAnnotation(MyTransactional.class) != null) {
                    hasMyTransaction.set(true);
                }
            });
        });
        return hasMyTransaction.get();
    }

    @Bean
    public MyTransactionalBeanPostProcessor myTransactionalBeanPostProcessor() {
        return new MyTransactionalBeanPostProcessor();
    }

    public class MyTransactionalBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (transactional(bean)) {
                ProxyFactory proxyFactory = new ProxyFactory();
                proxyFactory.setInterfaces(bean.getClass().getInterfaces());
                proxyFactory.setTarget(bean);
                proxyFactory.addAdvice((MethodInterceptor) methodInvocation -> {
                    Method method = methodInvocation.getMethod();
                    Object[] arguments = methodInvocation.getArguments();
                    try {
                        if (method.getAnnotation(MyTransactional.class) != null) {
                            log.info("start transaction for method: {} ", method.getName());
                        }
                        return method.invoke(bean, arguments);
                    } finally {
                        if (method.getAnnotation(MyTransactional.class) != null) {
                            log.info("finish transaction for method: {}", method.getName());
                        }
                    }
                });
                return proxyFactory.getProxy(bean.getClass().getClassLoader());
            }
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }
    }

    @Bean
    ApplicationRunner applicationRunner(DefaultConsumerService consumerService) {
        return args -> {
            consumerService.create();
            consumerService.add();
        };
    }

    @Bean
    public DefaultConsumerService defaultConsumerService() {
        return new DefaultConsumerService();
    }

    public static class DefaultConsumerService {
        @MyTransactional
        public void create() {
            log.info("call method create");
        }

        public void add() {
            log.info("call method add");
        }
    }

}
