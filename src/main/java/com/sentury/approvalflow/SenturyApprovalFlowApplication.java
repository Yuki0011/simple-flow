package com.sentury.approvalflow;



import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableDubbo
@SpringBootApplication
@EnableCaching
public class SenturyApprovalFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(SenturyApprovalFlowApplication.class, args);
    }
}
