package com.tezzasolutions.lendingapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class LendingappApplicationTests {

	@Test
	void contextLoads() {
		// This test verifies that the Spring context loads successfully
	}

	@Test
	void mainMethodRuns() {
		LendingappApplication.main(new String[]{});
	}
}
