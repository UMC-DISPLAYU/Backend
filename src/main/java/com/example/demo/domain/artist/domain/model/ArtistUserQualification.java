package com.example.demo.domain.artist.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;

@Getter
public class ArtistUserQualification {

  private final Long userId;
  private String schoolEmail;
  private String universityName;
  private boolean schoolVerified;
  private boolean withdrawn;
  private long sourceRevision;
  private UUID lastEventId;
  private Instant updatedAt;

  private ArtistUserQualification(
      Long userId,
      String schoolEmail,
      String universityName,
      boolean schoolVerified,
      boolean withdrawn,
      long sourceRevision,
      UUID lastEventId,
      Instant updatedAt) {
    this.userId = Objects.requireNonNull(userId, "사용자 ID는 필수입니다.");
    this.schoolEmail = schoolEmail;
    this.universityName = universityName;
    this.schoolVerified = schoolVerified;
    this.withdrawn = withdrawn;
    this.sourceRevision = sourceRevision;
    this.lastEventId = lastEventId;
    this.updatedAt = Objects.requireNonNull(updatedAt, "갱신 시각은 필수입니다.");
  }

  public static ArtistUserQualification empty(Long userId, Instant updatedAt) {
    return new ArtistUserQualification(userId, null, null, false, false, -1L, null, updatedAt);
  }

  public static ArtistUserQualification restore(
      Long userId,
      String schoolEmail,
      String universityName,
      boolean schoolVerified,
      boolean withdrawn,
      long sourceRevision,
      UUID lastEventId,
      Instant updatedAt) {
    return new ArtistUserQualification(
        userId,
        schoolEmail,
        universityName,
        schoolVerified,
        withdrawn,
        sourceRevision,
        lastEventId,
        updatedAt);
  }

  public boolean applySchoolVerification(
      UUID eventId, String schoolEmail, String universityName, long revision, Instant occurredAt) {
    if (!canApply(eventId, revision)) {
      return false;
    }
    this.schoolEmail = requireNonBlank(schoolEmail, "학교 이메일은 필수입니다.");
    this.universityName = requireNonBlank(universityName, "대학명은 필수입니다.");
    this.schoolVerified = true;
    markApplied(eventId, revision, occurredAt);
    return true;
  }

  public boolean applyUniversityChange(
      UUID eventId, String universityName, long revision, Instant occurredAt) {
    if (!canApply(eventId, revision)) {
      return false;
    }
    this.universityName = requireNonBlank(universityName, "대학명은 필수입니다.");
    markApplied(eventId, revision, occurredAt);
    return true;
  }

  public boolean applyWithdrawal(UUID eventId, long revision, Instant occurredAt) {
    if (!canApply(eventId, revision)) {
      return false;
    }
    this.withdrawn = true;
    markApplied(eventId, revision, occurredAt);
    return true;
  }

  private boolean canApply(UUID eventId, long revision) {
    Objects.requireNonNull(eventId, "이벤트 ID는 필수입니다.");
    return !eventId.equals(lastEventId) && revision > sourceRevision;
  }

  private void markApplied(UUID eventId, long revision, Instant occurredAt) {
    this.lastEventId = eventId;
    this.sourceRevision = revision;
    this.updatedAt = Objects.requireNonNull(occurredAt, "이벤트 발생 시각은 필수입니다.");
  }

  private static String requireNonBlank(String value, String message) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(message);
    }
    return value;
  }
}
