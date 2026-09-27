package com.example.demo.domain.user.application.event;

import com.example.demo.domain.artist.contract.event.v1.ArtistProfileCreatedEvent;
import com.example.demo.domain.artist.contract.event.v1.ArtistQualificationChangedEvent;
import com.example.demo.domain.user.application.port.UserArtistEventProjector;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserArtistCompatibilityProjector implements UserArtistEventProjector {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public void project(ArtistProfileCreatedEvent event) {
    findUser(event.userId()).completeArtistVerification();
  }

  @Override
  @Transactional
  public void project(ArtistQualificationChangedEvent event) {
    findUser(event.userId()).changeArtistVerification(event.qualified());
  }

  private User findUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
  }
}
