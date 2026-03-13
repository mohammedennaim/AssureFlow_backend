package com.pfe.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://localhost:3000}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-origin-patterns:http://localhost:*,https://localhost:*}")
    private String[] allowedOriginPatterns;

    @Value("${app.cors.max-age:3600}")
    private Long maxAge;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Allow credentials (cookies, authorization headers)
        corsConfig.setAllowCredentials(true);
        
        // Allowed origins (exact matches)
        corsConfig.setAllowedOrigins(Arrays.asList(allowedOrigins));
        
        // Allowed origin patterns (wildcard support)
        corsConfig.setAllowedOriginPatterns(Arrays.asList(allowedOriginPatterns));
        
        // Allowed HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));
        
        // Allowed headers
        corsConfig.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type", 
                "Accept",
                "Origin",
                "X-Requested-With",
                "X-Correlation-ID",
                "X-User-ID",
                "X-User-Role",
                "Cache-Control",
                "Pragma",
                "Expires"
        ));
        
        // Exposed headers (visible to frontend)
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count",
                "X-Correlation-ID",
                "X-Rate-Limit-Remaining",
                "X-Rate-Limit-Reset",
                "Location"
        ));
        
        // Cache preflight requests for 1 hour
        corsConfig.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        log.info("CORS configured with allowed origins: {}", Arrays.toString(allowedOrigins));
        log.info("CORS configured with allowed origin patterns: {}", Arrays.toString(allowedOriginPatterns));

        return new CorsWebFilter(source);
    }
}