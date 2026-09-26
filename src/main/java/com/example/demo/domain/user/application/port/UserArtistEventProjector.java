package com.example.demo.domain.user.application.port;

import com.example.demo.domain.artist.application.event.ArtistProfileCreatedEvent;
import com.example.demo.domain.artist.application.event.ArtistQualificationChangedEvent;

public interface UserArtistEventProjector {

  void project(ArtistProfileCreatedEvent event);

  void project(ArtistQualificationChangedEvent event);
}
