package com.example.demo.domain.user.application.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record UserSchoolVerifiedEvent(
    UUID eventId,
    Long userId,
    String schoolEmail,
    String universityName,
    long revision,
    Instant occurredAt) {

  public UserSchoolVerifiedEvent {
    Objects.requireNonNull(eventId, "이벤트 ID는 필수입니다.");
    Objects.requireNonNull(userId, "사용자 ID는 필수입니다.");
    requireNonBlank(schoolEmail, "학교 이메일은 필수입니다.");
    requireNonBlank(universityName, "대학명은 필수입니다.");
    requireNonNegative(revision);
    Objects.requireNonNull(occurredAt, "이벤트 발생 시각은 필수입니다.");
  }

  private static void requireNonBlank(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
  }

  private static void requireNonNegative(long revision) {
    if (revision < 0) {
      throw new IllegalArgumentException("이벤트 revision은 0 이상이어야 합니다.");
    }
  }
}
