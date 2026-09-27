package com.example.demo.domain.artist.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.artist.application.command.CreateArtistProfileCommand;
import com.example.demo.domain.artist.application.permission.ArtistPermissionChecker;
import com.example.demo.domain.artist.contract.event.v1.ArtistProfileCreatedEvent;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.model.ArtistUserQualification;
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.artist.domain.repository.ArtistUserQualificationRepository;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.example.demo.domain.artist.presentation.mapper.ArtistProfileMapper;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

class CreateArtistProfileServiceTest {

  private static final Instant NOW = Instant.parse("2026-09-27T00:00:00Z");
  private final ArtistUserQualificationRepository qualificationRepository =
      mock(ArtistUserQualificationRepository.class);
  private final ArtistProfileRepository artistProfileRepository =
      mock(ArtistProfileRepository.class);
  private final AreaOfActivityRepository areaOfActivityRepository =
      mock(AreaOfActivityRepository.class);
  private final ArtistProfileMapper artistProfileMapper = mock(ArtistProfileMapper.class);
  private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
  private final CreateArtistProfileService service =
      new CreateArtistProfileService(
          qualificationRepository,
          artistProfileRepository,
          areaOfActivityRepository,
          artistProfileMapper,
          new ArtistPermissionChecker(),
          eventPublisher,
          Clock.fixed(NOW, ZoneOffset.UTC));

  @Test
  void createsProfileFromLocalQualificationAndPublishesEvent() {
    Long userId = 1L;
    ArtistUserQualification qualification =
        ArtistUserQualification.restore(
            userId, "student@du.ac.kr", "덕성여자대학교", true, false, 1L, null, NOW);
    CreateArtistProfileCommand command =
        CreateArtistProfileCommand.builder()
            .artistName("artist")
            .activityCategories(List.of(ActivityCategory.PAINTING))
            .build();
    ArtistProfile profile = mock(ArtistProfile.class);

    when(qualificationRepository.findByUserId(userId)).thenReturn(Optional.of(qualification));
    when(artistProfileRepository.findByUserId(userId)).thenReturn(Optional.empty());
    when(artistProfileRepository.existsByArtistName("artist")).thenReturn(false);
    when(artistProfileMapper.toEntity(
            userId, qualification.getSchoolEmail(), qualification.getUniversityName(), command))
        .thenReturn(profile);
    when(artistProfileRepository.save(profile)).thenReturn(profile);
    when(profile.getId()).thenReturn(10L);
    when(profile.getArtistName()).thenReturn("artist");

    ArtistProfile result = service.execute(userId, command);

    assertThat(result).isSameAs(profile);
    verify(artistProfileRepository).save(profile);
    verify(eventPublisher).publishEvent(isA(ArtistProfileCreatedEvent.class));
  }
}
