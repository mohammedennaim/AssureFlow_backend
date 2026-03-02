package com.pfe.workflow.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI workflowServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Workflow Service API")
                        .description("Insurance Workflow Management Service")
                        .version("1.0.0"));
    }
}
