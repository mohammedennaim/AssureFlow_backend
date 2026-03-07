package com.pfe.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.pfe")
@ComponentScan(basePackages = { "com.pfe.billing", "com.pfe.commons" })
@EnableFeignClients
@EnableCaching
public class BillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingApplication.class, args);
    }
}
