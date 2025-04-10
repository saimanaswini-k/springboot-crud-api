package com.example.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Minimal configuration for controller tests
 * This avoids loading the database configuration which isn't needed for controller tests
 */
@TestConfiguration
@EnableWebMvc
public class ControllerTestConfig {
    // Add any beans specifically needed for controller tests here
}
