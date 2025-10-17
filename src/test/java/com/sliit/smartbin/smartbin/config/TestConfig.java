package com.sliit.smartbin.smartbin.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Test configuration for unit tests
 * Provides fixed clock for deterministic testing
 */
@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public Clock fixedClock() {
        // Fixed clock for deterministic testing
        return Clock.fixed(Instant.parse("2024-01-15T10:00:00Z"), ZoneId.of("UTC"));
    }
}

