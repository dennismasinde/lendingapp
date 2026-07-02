package com.tezzasolutions.lendingapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestAuditConfig.class)
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
