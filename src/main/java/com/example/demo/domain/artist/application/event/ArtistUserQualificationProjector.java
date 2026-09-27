package com.example.demo.domain.artist.application.event;

import com.example.demo.domain.artist.application.port.ArtistUserEventProjector;
import com.example.demo.domain.artist.domain.model.ArtistUserQualification;
import com.example.demo.domain.artist.domain.repository.ArtistUserQualificationRepository;
import com.example.demo.domain.user.contract.event.v1.UserSchoolVerifiedEvent;
import com.example.demo.domain.user.contract.event.v1.UserUniversityChangedEvent;
import com.example.demo.domain.user.contract.event.v1.UserWithdrawnEvent;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtistUserQualificationProjector implements ArtistUserEventProjector {

  private final ArtistUserQualificationRepository repository;

  @Override
  @Transactional
  public void project(UserSchoolVerifiedEvent event) {
    ArtistUserQualification qualification = findOrCreate(event.userId(), event.occurredAt());
    if (qualification.applySchoolVerification(
        event.eventId(),
        event.schoolEmail(),
        event.universityName(),
        event.revision(),
        event.occurredAt())) {
      repository.save(qualification);
    }
  }

  @Override
  @Transactional
  public void project(UserUniversityChangedEvent event) {
    ArtistUserQualification qualification = findOrCreate(event.userId(), event.occurredAt());
    if (qualification.applyUniversityChange(
        event.eventId(), event.universityName(), event.revision(), event.occurredAt())) {
      repository.save(qualification);
    }
  }

  @Override
  @Transactional
  public void project(UserWithdrawnEvent event) {
    ArtistUserQualification qualification = findOrCreate(event.userId(), event.occurredAt());
    if (qualification.applyWithdrawal(event.eventId(), event.revision(), event.occurredAt())) {
      repository.save(qualification);
    }
  }

  private ArtistUserQualification findOrCreate(Long userId, Instant occurredAt) {
    return repository
        .findByUserId(userId)
        .orElseGet(() -> ArtistUserQualification.empty(userId, occurredAt));
  }
}
