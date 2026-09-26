package com.example.demo.domain.artist.application.event;

import com.example.demo.domain.artist.application.port.ArtistUserEventProjector;
import com.example.demo.domain.user.application.event.UserSchoolVerifiedEvent;
import com.example.demo.domain.user.application.event.UserUniversityChangedEvent;
import com.example.demo.domain.user.application.event.UserWithdrawnEvent;
import java.util.Objects;

public class ArtistUserEventHandler {

  private final ArtistUserEventProjector projector;

  public ArtistUserEventHandler(ArtistUserEventProjector projector) {
    this.projector = Objects.requireNonNull(projector, "이벤트 Projector는 필수입니다.");
  }

  public void handle(UserSchoolVerifiedEvent event) {
    projector.project(event);
  }

  public void handle(UserUniversityChangedEvent event) {
    projector.project(event);
  }

  public void handle(UserWithdrawnEvent event) {
    projector.project(event);
  }
}
