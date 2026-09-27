package com.example.demo.domain.user.application.event;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.demo.domain.artist.contract.event.v1.ArtistProfileCreatedEvent;
import com.example.demo.domain.user.contract.event.v1.UserSchoolVerifiedEvent;
import com.example.demo.domain.user.contract.event.v1.UserWithdrawnEvent;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class IntegrationEventContractTest {

  private static final Instant OCCURRED_AT = Instant.parse("2026-09-26T00:00:00Z");

  @Test
  void rejectsMissingRequiredValue() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> new UserWithdrawnEvent(null, 1L, 1L, OCCURRED_AT));

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () ->
                new UserSchoolVerifiedEvent(
                    UUID.randomUUID(), 1L, " ", "덕성여자대학교", 1L, OCCURRED_AT));

    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(
            () -> new ArtistProfileCreatedEvent(UUID.randomUUID(), 10L, 1L, " ", 1L, OCCURRED_AT));
  }

  @Test
  void rejectsNegativeRevision() {
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> new UserWithdrawnEvent(UUID.randomUUID(), 1L, -1L, OCCURRED_AT));
  }
}
