package com.example.demo.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.data.auditing.DateTimeProvider;

class JpaAuditingConfigTest {

  @Test
  void providesUtcTime() {
    LocalDateTime before = LocalDateTime.now(ZoneOffset.UTC);
    DateTimeProvider provider = new JpaAuditingConfig().auditingDateTimeProvider();
    LocalDateTime provided = (LocalDateTime) provider.getNow().orElseThrow();
    LocalDateTime after = LocalDateTime.now(ZoneOffset.UTC);

    assertThat(provided).isBetween(before, after);
  }
}
