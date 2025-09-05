package com.jiandong.aop;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

	private final Logger log = LoggerFactory.getLogger(LogAspect.this.getClass());

	@Pointcut("@annotation(com.jiandong.aop.Log)")
	public void logPointcut() {
	}

	@Before("logPointcut()")
	public void log(JoinPoint joinPoint) {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		log.info(String.format("before execute method: %s, parameters: %s, argus:%s",
				method.getName(),
				Arrays.toString(method.getParameters()),
				Arrays.toString(joinPoint.getArgs()))
		);
	}

}
