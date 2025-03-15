package com.sentury.approvalflow.config;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

//@Aspect
@Slf4j
//@Component
public class LogAop {


    @Around("execution(* com.sentury.approvalflow.web.controller.*.*(..))")
    @SneakyThrows
    public Object writeLog(ProceedingJoinPoint point) {
        try {
           // return LogAopUtil.write(point);
            return point.proceed();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

}
