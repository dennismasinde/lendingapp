package com.tezzasolutions.lendingapp;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "testAuditorProvider")
public class TestAuditConfig {

    @Bean
    public AuditorAware<Long> testAuditorProvider() {
        return () -> Optional.of(1L);
    }
}