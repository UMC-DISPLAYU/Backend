package com.example.demo.domain.user.application.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record UserWithdrawnEvent(UUID eventId, Long userId, long revision, Instant occurredAt) {

  public UserWithdrawnEvent {
    Objects.requireNonNull(eventId, "이벤트 ID는 필수입니다.");
    Objects.requireNonNull(userId, "사용자 ID는 필수입니다.");
    if (revision < 0) {
      throw new IllegalArgumentException("이벤트 revision은 0 이상이어야 합니다.");
    }
    Objects.requireNonNull(occurredAt, "이벤트 발생 시각은 필수입니다.");
  }
}
