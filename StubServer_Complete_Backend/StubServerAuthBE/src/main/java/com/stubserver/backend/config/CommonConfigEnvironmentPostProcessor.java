package com.stubserver.backend.config;

import org.common.db.config.ConfigLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * Bridges the ENV-SPECIFIC keys from the common hibernate {@code config.properties}
 * into Spring's Environment (cors-origin, prod-url, core-server and the
 * web-server port). Static config (spring.mail, jwt, file paths, mail.from) lives in
 * the backend's application.yml and is read by Spring directly — this bridge does not
 * touch it.
 *
 * <p>The web-server port is stored as {@code portalserver.port} in config.properties
 * and is mapped here to Spring's standard {@code server.port}.
 *
 * <p>Runs before the application context is created and registers the loaded
 * properties as the highest-precedence property source.
 */
public class CommonConfigEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Properties source = ConfigLoader.properties;   // the single config.properties (hibernate jar)
        if (source == null) {
            return;
        }

        Properties resolved = new Properties();
        for (String key : source.stringPropertyNames()) {
            resolved.put(key, source.getProperty(key));
        }

        // Map the renamed port key to Spring's standard server.port.
        String port = source.getProperty("portalserver.port");
        if (port != null && !port.isBlank()) {
            resolved.put("server.port", port.trim());
        }

        // addFirst => highest precedence (env-specific values win over application.yml).
        environment.getPropertySources()
                .addFirst(new PropertiesPropertySource("commonConfigProperties", resolved));
    }

    @Override
    public int getOrder() {
        // Run after Spring Boot has loaded application.yml so addFirst wins cleanly.
        return Ordered.LOWEST_PRECEDENCE;
    }
}
