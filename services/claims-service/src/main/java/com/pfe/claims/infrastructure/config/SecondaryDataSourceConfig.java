package com.pfe.claims.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecondaryDataSourceConfig {

    @Value("${secondary.datasource.url}")
    private String url;

    @Value("${secondary.datasource.username}")
    private String username;

    @Value("${secondary.datasource.password}")
    private String password;

    @Bean(name = "clientDataSource")
    public DataSource clientDataSource() {
        var hikariConfig = new com.zaxxer.hikari.HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setMaximumPoolSize(5);
        hikariConfig.setConnectionTimeout(5000);

        log.info("[DB] Initializing secondary datasource for client_db");
        return new com.zaxxer.hikari.HikariDataSource(hikariConfig);
    }

    @Bean(name = "clientJdbcTemplate")
    public JdbcTemplate clientJdbcTemplate(@Qualifier("clientDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
