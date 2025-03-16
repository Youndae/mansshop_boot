package com.example.mansshop_boot.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TimeCheckAOP {

    @Around("execution(* com.example.mansshop_boot..*(..)) && !execution(* org.springframework..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try{
            return joinPoint.proceed();
        }catch (Exception e) {
            log.error("AOP exception!");
            throw e;
        }finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;

            if(timeMs > 1000)
                log.warn("{} --- time = {}ms", joinPoint.getSignature().toShortString(), timeMs);
            else
                log.info("{} --- time = {}ms", joinPoint.getSignature().toShortString(), timeMs);
        }
    }
}
