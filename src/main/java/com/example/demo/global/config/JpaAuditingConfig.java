package com.example.demo.global.config;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaAuditingConfig {

  @Bean
  public DateTimeProvider auditingDateTimeProvider() {
    return () -> Optional.of(LocalDateTime.now(ZoneOffset.UTC));
  }
}
