package com.pfe.workflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.pfe")
@ComponentScan(basePackages = { "com.pfe.workflow", "com.pfe.commons" })
@EnableCaching
public class WorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
    }
}
