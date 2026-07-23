package com.stubserver.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// Database connectivity is owned by the common Hibernate framework, so Spring's
// own DataSource auto-configuration is disabled here.
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties
public class StubServerBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(StubServerBackendApplication.class, args);
    }
}
