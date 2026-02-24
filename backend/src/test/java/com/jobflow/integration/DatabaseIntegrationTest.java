package com.jobflow.integration;

import com.jobflow.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class DatabaseIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.flyway.url", postgres::getJdbcUrl);
    registry.add("spring.flyway.user", postgres::getUsername);
    registry.add("spring.flyway.password", postgres::getPassword);
    // Desativar Redis nos testes de banco se necessário, ou usar um RedisContainer
    registry.add("spring.data.redis.repositories.enabled", () -> "false");
  }

  @Autowired
  private UserRepository userRepository;

  @Test
  void testFlywayMigrationsAndDatabaseConnection() {
    // Se este teste rodar, significa que:
    // 1. O contexto do Spring subiu
    // 2. O Testcontainers subiu o Postgres
    // 3. O Flyway executou as migrations com sucesso
    long count = userRepository.count();
    assertThat(count).isGreaterThanOrEqualTo(0);
  }
}
