package com.pfe.client.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Client Service API")
                        .description("API for managing insurance clients in AssureFlow platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AssureFlow Team")
                                .email("contact@assureflow.com"))
                        .license(new License()
                                .name("AssureFlow Proprietary")
                                .url("https://assureflow.com")));
    }
}
