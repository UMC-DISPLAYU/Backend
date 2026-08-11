package com.example.demo.global.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.health.presentation.HealthController.HealthResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.response.ResultType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class JacksonConfigTest {

  @Test
  void serializesUtcApiLocalDateTimeWithUtcOffset() throws Exception {
    JsonMapper.Builder builder = JsonMapper.builder();
    new JacksonConfig().utcLocalDateTimeCustomizer().customize(builder);
    LocalDateTime utcTime = LocalDateTime.parse("2026-08-09T16:45:05");

    String json =
        builder
            .build()
            .writeValueAsString(
                new ApiResponseBody<>(
                    ResultType.SUCCESS,
                    new ApiResponseBody.SuccessBody<>(new HealthResponse("UP", utcTime)),
                    null,
                    new ApiResponseBody.Meta(utcTime, "/api/v1/health")));

    assertThat(json).contains("\"checkedAt\":\"2026-08-09T16:45:05Z\"");
    assertThat(json).contains("\"timestamp\":\"2026-08-09T16:45:05Z\"");
  }

  @Test
  void doesNotLabelUnverifiedLocalDateTimeAsUtc() throws Exception {
    JsonMapper.Builder builder = JsonMapper.builder();
    new JacksonConfig().utcLocalDateTimeCustomizer().customize(builder);

    String json =
        builder
            .build()
            .writeValueAsString(new LegacyResponse(LocalDateTime.parse("2026-08-09T16:45:05")));

    assertThat(json).contains("\"occurredAt\":\"2026-08-09T16:45:05\"");
  }

  private record LegacyResponse(LocalDateTime occurredAt) {}
}
