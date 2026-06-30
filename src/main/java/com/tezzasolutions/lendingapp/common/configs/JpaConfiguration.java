package com.tezzasolutions.lendingapp.common.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableJpaRepositories(
        basePackages = {
                "com.tezzasolutions.lendingapp.customer",
                "com.tezzasolutions.lendingapp.loan",
                "com.tezzasolutions.lendingapp.fee",
                "com.tezzasolutions.lendingapp.installment",
                "com.tezzasolutions.lendingapp.repayment",
                "com.tezzasolutions.lendingapp.notification"
        }
)
@EnableTransactionManagement
public class JpaConfiguration {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }
}
