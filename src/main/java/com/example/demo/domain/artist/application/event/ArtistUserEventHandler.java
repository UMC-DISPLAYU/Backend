package com.example.demo.domain.artist.application.event;

import com.example.demo.domain.artist.application.port.ArtistUserEventProjector;
import com.example.demo.domain.user.contract.event.v1.UserSchoolVerifiedEvent;
import com.example.demo.domain.user.contract.event.v1.UserUniversityChangedEvent;
import com.example.demo.domain.user.contract.event.v1.UserWithdrawnEvent;
import java.util.Objects;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class ArtistUserEventHandler {

  // User 이벤트를 받아 Artist 측 Projector에 전달
  private final ArtistUserEventProjector projector;

  public ArtistUserEventHandler(ArtistUserEventProjector projector) {
    this.projector = Objects.requireNonNull(projector, "이벤트 Projector는 필수입니다.");
  }

  @ApplicationModuleListener
  public void handle(UserSchoolVerifiedEvent event) {
    projector.project(event);
  }

  @ApplicationModuleListener
  public void handle(UserUniversityChangedEvent event) {
    projector.project(event);
  }

  @ApplicationModuleListener
  public void handle(UserWithdrawnEvent event) {
    projector.project(event);
  }
}
