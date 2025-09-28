package com.jiandong.core.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aopalliance.intercept.MethodInterceptor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

@Configuration
public class SpringProxy {

	private static final Logger log = LoggerFactory.getLogger(SpringProxy.class);

	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@Documented
	@Reflective
	public @interface MyTransactional {

	}

	@Bean
	public BeanPostProcessor myTransactionalBeanPostProcessor() {
		return new BeanPostProcessor() {

			@Override
			public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
				if (hasMyTransactional(bean)) {
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
						}
						finally {
							if (method.getAnnotation(MyTransactional.class) != null) {
								log.info("finish transaction for method: {}", method.getName());
							}
						}
					});
					return proxyFactory.getProxy(bean.getClass().getClassLoader());
				}
				return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
			}
		};
	}

	static boolean hasMyTransactional(Object obj) {
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

}
