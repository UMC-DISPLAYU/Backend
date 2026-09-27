package com.example.demo.domain.artist.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ArtistUserQualificationTest {

  private static final Long USER_ID = 1L;
  private static final Instant INITIAL_TIME = Instant.parse("2026-09-27T00:00:00Z");

  @Test
  void appliesSchoolVerificationUniversityChangeAndWithdrawalInRevisionOrder() {
    ArtistUserQualification qualification = ArtistUserQualification.empty(USER_ID, INITIAL_TIME);

    qualification.applySchoolVerification(
        UUID.randomUUID(), "student@du.ac.kr", "덕성여자대학교", 1L, INITIAL_TIME.plusSeconds(1));
    qualification.applyUniversityChange(
        UUID.randomUUID(), "변경대학교", 2L, INITIAL_TIME.plusSeconds(2));
    qualification.applyWithdrawal(UUID.randomUUID(), 3L, INITIAL_TIME.plusSeconds(3));

    assertThat(qualification.getSchoolEmail()).isEqualTo("student@du.ac.kr");
    assertThat(qualification.getUniversityName()).isEqualTo("변경대학교");
    assertThat(qualification.isSchoolVerified()).isTrue();
    assertThat(qualification.isWithdrawn()).isTrue();
    assertThat(qualification.getSourceRevision()).isEqualTo(3L);
  }

  @Test
  void ignoresDuplicateEventAndLowerOrEqualRevision() {
    ArtistUserQualification qualification = ArtistUserQualification.empty(USER_ID, INITIAL_TIME);
    UUID appliedEventId = UUID.randomUUID();
    qualification.applySchoolVerification(
        appliedEventId, "student@du.ac.kr", "덕성여자대학교", 5L, INITIAL_TIME.plusSeconds(5));

    boolean duplicateApplied =
        qualification.applyUniversityChange(
            appliedEventId, "중복대학교", 6L, INITIAL_TIME.plusSeconds(6));
    boolean lowerApplied =
        qualification.applyUniversityChange(
            UUID.randomUUID(), "과거대학교", 4L, INITIAL_TIME.plusSeconds(4));
    boolean equalApplied =
        qualification.applyWithdrawal(UUID.randomUUID(), 5L, INITIAL_TIME.plusSeconds(5));

    assertThat(duplicateApplied).isFalse();
    assertThat(lowerApplied).isFalse();
    assertThat(equalApplied).isFalse();
    assertThat(qualification.getUniversityName()).isEqualTo("덕성여자대학교");
    assertThat(qualification.isWithdrawn()).isFalse();
    assertThat(qualification.getSourceRevision()).isEqualTo(5L);
  }
}
