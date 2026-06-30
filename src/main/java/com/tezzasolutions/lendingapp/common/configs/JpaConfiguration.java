package com.tezzasolutions.lendingapp.common.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.tezzasolutions.lendingapp.repository")
@EnableTransactionManagement
public class JpaConfiguration {
    // Additional JPA configuration if needed
}
