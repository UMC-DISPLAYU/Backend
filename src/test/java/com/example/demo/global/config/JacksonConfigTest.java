package com.example.demo.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class JacksonConfigTest {

  @Test
  void serializesLocalDateTimeWithUtcOffset() throws Exception {
    JsonMapper.Builder builder = JsonMapper.builder();
    new JacksonConfig().utcLocalDateTimeCustomizer().customize(builder);

    String json = builder.build().writeValueAsString(LocalDateTime.parse("2026-08-09T16:45:05"));

    assertThat(json).isEqualTo("\"2026-08-09T16:45:05Z\"");
  }
}
