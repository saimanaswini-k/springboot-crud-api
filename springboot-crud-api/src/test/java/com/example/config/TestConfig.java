package com.example.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

/**
 * Minimal test configuration for tests
 * Letting Spring Boot's auto-configuration handle most of the setup
 */
@TestConfiguration
@ActiveProfiles("test")
@EnableJpaRepositories(basePackages = {"com.example.repository"})
@ComponentScan(basePackages = {"com.example.entity", "com.example.repository"})
public class TestConfig {
    // Let Spring Boot auto-configure the test environment
}
