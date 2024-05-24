package com.jiandong.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@Profile("log-aspect")
public class LogAspect {

    @Pointcut("@annotation(com.mjd507.aop.Log)")
    public void logPointcut() {}

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
