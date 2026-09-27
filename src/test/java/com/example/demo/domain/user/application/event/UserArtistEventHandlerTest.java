package com.example.demo.domain.user.application.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.artist.contract.event.v1.ArtistProfileCreatedEvent;
import com.example.demo.domain.artist.contract.event.v1.ArtistQualificationChangedEvent;
import com.example.demo.domain.user.application.port.UserArtistEventProjector;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserArtistEventHandlerTest {

  private final UserArtistEventProjector projector = mock(UserArtistEventProjector.class);
  private final UserArtistEventHandler handler = new UserArtistEventHandler(projector);

  @Test
  void delegatesArtistEventsToUserProjector() {
    Instant occurredAt = Instant.parse("2026-09-26T00:00:00Z");
    ArtistProfileCreatedEvent profileCreatedEvent =
        new ArtistProfileCreatedEvent(UUID.randomUUID(), 10L, 1L, "artist", 1L, occurredAt);
    ArtistQualificationChangedEvent qualificationChangedEvent =
        new ArtistQualificationChangedEvent(UUID.randomUUID(), 10L, 1L, false, 2L, occurredAt);

    handler.handle(profileCreatedEvent);
    handler.handle(qualificationChangedEvent);

    verify(projector).project(profileCreatedEvent);
    verify(projector).project(qualificationChangedEvent);
  }
}
