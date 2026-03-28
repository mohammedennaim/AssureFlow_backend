package com.pfe.claims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.pfe")
@EnableFeignClients
@EnableScheduling
public class ClaimsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClaimsApplication.class, args);
    }
}
