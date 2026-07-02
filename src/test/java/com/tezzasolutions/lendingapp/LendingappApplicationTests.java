package com.tezzasolutions.lendingapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
