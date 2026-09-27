package com.example.demo.domain.user.application.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artist.contract.event.v1.ArtistProfileCreatedEvent;
import com.example.demo.domain.artist.contract.event.v1.ArtistQualificationChangedEvent;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserArtistCompatibilityProjectorTest {

  private static final Instant OCCURRED_AT = Instant.parse("2026-09-27T00:00:00Z");
  private final UserRepository userRepository = mock(UserRepository.class);
  private final UserArtistCompatibilityProjector projector =
      new UserArtistCompatibilityProjector(userRepository);

  @Test
  void reflectsArtistProfileCreationAndQualificationChangesInCompatibilityField() {
    User user = User.builder().id(1L).isVerified(false).build();
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    projector.project(
        new ArtistProfileCreatedEvent(UUID.randomUUID(), 10L, 1L, "artist", 1L, OCCURRED_AT));
    assertThat(user.isVerified()).isTrue();

    projector.project(
        new ArtistQualificationChangedEvent(
            UUID.randomUUID(), 10L, 1L, false, 2L, OCCURRED_AT.plusSeconds(1)));
    assertThat(user.isVerified()).isFalse();
  }
}
