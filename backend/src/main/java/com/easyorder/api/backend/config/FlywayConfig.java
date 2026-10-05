package com.easyorder.api.backend.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", havingValue = "true", matchIfMissing = true)
public class FlywayConfig {

    @Bean(name = "flywayDataSource")
    @FlywayDataSource
    public DataSource flywayDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.flyway.username:${spring.flyway.user:${spring.datasource.username}}}") String username,
            @Value("${spring.flyway.password:${spring.datasource.password}}") String password) {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Profile("dev")
    @Bean
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            if (Boolean.parseBoolean(System.getenv("RESET_DB"))) {
                flyway.clean();
            }
            flyway.migrate();
        };
    }
}