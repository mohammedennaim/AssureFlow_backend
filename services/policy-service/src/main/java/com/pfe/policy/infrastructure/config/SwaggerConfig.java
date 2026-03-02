package com.pfe.policy.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI policyServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Policy Service API")
                        .description("Insurance Policy Management Service")
                        .version("1.0.0"));
    }
}
