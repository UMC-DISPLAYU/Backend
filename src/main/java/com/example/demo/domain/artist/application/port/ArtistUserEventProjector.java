package com.example.demo.domain.artist.application.port;

import com.example.demo.domain.user.application.event.UserSchoolVerifiedEvent;
import com.example.demo.domain.user.application.event.UserUniversityChangedEvent;
import com.example.demo.domain.user.application.event.UserWithdrawnEvent;

public interface ArtistUserEventProjector {

  void project(UserSchoolVerifiedEvent event);

  void project(UserUniversityChangedEvent event);

  void project(UserWithdrawnEvent event);
}
