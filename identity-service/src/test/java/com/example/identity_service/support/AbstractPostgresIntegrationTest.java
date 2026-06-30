package com.example.identity_service.support;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(org.testcontainers.junit.jupiter.TestcontainersExtension.class)
public abstract class AbstractPostgresIntegrationTest {

	@Container
	@SuppressWarnings("resource")
	private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16")
			.withDatabaseName("identity_db")
			.withUsername("identity_user")
			.withPassword("identity_pass");

	@DynamicPropertySource
	static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
		registry.add("spring.datasource.username", POSTGRES::getUsername);
		registry.add("spring.datasource.password", POSTGRES::getPassword);
	}
}
