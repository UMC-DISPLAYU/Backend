package com.example.demo.domain.user.application.event;

import com.example.demo.domain.artist.contract.event.v1.ArtistProfileCreatedEvent;
import com.example.demo.domain.artist.contract.event.v1.ArtistQualificationChangedEvent;
import com.example.demo.domain.user.application.port.UserArtistEventProjector;
import java.util.Objects;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class UserArtistEventHandler {

  // Artist 이벤트를 받아 User 측 Projector에 전달
  private final UserArtistEventProjector projector;

  public UserArtistEventHandler(UserArtistEventProjector projector) {
    this.projector = Objects.requireNonNull(projector, "이벤트 Projector는 필수입니다.");
  }

  @ApplicationModuleListener
  public void handle(ArtistProfileCreatedEvent event) {
    projector.project(event);
  }

  @ApplicationModuleListener
  public void handle(ArtistQualificationChangedEvent event) {
    projector.project(event);
  }
}
