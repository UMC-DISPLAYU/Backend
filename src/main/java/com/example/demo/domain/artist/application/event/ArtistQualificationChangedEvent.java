package com.example.demo.domain.artist.application.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ArtistQualificationChangedEvent(
    UUID eventId,
    Long artistProfileId,
    Long userId,
    boolean qualified,
    long revision,
    Instant occurredAt) {

  public ArtistQualificationChangedEvent {
    Objects.requireNonNull(eventId, "이벤트 ID는 필수입니다.");
    Objects.requireNonNull(artistProfileId, "작가 프로필 ID는 필수입니다.");
    Objects.requireNonNull(userId, "사용자 ID는 필수입니다.");
    if (revision < 0) {
      throw new IllegalArgumentException("이벤트 revision은 0 이상이어야 합니다.");
    }
    Objects.requireNonNull(occurredAt, "이벤트 발생 시각은 필수입니다.");
  }
}
