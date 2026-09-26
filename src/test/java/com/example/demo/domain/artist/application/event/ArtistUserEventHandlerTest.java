package com.example.demo.domain.artist.application.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.artist.application.port.ArtistUserEventProjector;
import com.example.demo.domain.user.application.event.UserSchoolVerifiedEvent;
import com.example.demo.domain.user.application.event.UserUniversityChangedEvent;
import com.example.demo.domain.user.application.event.UserWithdrawnEvent;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ArtistUserEventHandlerTest {

  private final ArtistUserEventProjector projector = mock(ArtistUserEventProjector.class);
  private final ArtistUserEventHandler handler = new ArtistUserEventHandler(projector);

  @Test
  void delegatesUserEventsToArtistProjector() {
    Instant occurredAt = Instant.parse("2026-09-26T00:00:00Z");
    UserSchoolVerifiedEvent schoolVerifiedEvent =
        new UserSchoolVerifiedEvent(
            UUID.randomUUID(), 1L, "student@du.ac.kr", "덕성여자대학교", 1L, occurredAt);
    UserUniversityChangedEvent universityChangedEvent =
        new UserUniversityChangedEvent(UUID.randomUUID(), 1L, "변경대학교", 2L, occurredAt);
    UserWithdrawnEvent withdrawnEvent =
        new UserWithdrawnEvent(UUID.randomUUID(), 1L, 3L, occurredAt);

    handler.handle(schoolVerifiedEvent);
    handler.handle(universityChangedEvent);
    handler.handle(withdrawnEvent);

    verify(projector).project(schoolVerifiedEvent);
    verify(projector).project(universityChangedEvent);
    verify(projector).project(withdrawnEvent);
  }
}
