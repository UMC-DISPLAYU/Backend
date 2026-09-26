package com.example.demo.domain.user.application.event;

import com.example.demo.domain.artist.application.event.ArtistProfileCreatedEvent;
import com.example.demo.domain.artist.application.event.ArtistQualificationChangedEvent;
import com.example.demo.domain.user.application.port.UserArtistEventProjector;
import java.util.Objects;

public class UserArtistEventHandler {

  private final UserArtistEventProjector projector;

  public UserArtistEventHandler(UserArtistEventProjector projector) {
    this.projector = Objects.requireNonNull(projector, "이벤트 Projector는 필수입니다.");
  }

  public void handle(ArtistProfileCreatedEvent event) {
    projector.project(event);
  }

  public void handle(ArtistQualificationChangedEvent event) {
    projector.project(event);
  }
}
