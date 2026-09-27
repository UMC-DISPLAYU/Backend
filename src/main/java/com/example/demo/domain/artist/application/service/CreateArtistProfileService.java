package com.example.demo.domain.artist.application.service;

import com.example.demo.domain.artist.application.command.CreateArtistProfileCommand;
import com.example.demo.domain.artist.application.permission.ArtistPermissionChecker;
import com.example.demo.domain.artist.contract.event.v1.ArtistProfileCreatedEvent;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.entity.AreaOfActivity;
import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.artist.domain.repository.ArtistUserQualificationRepository;
import com.example.demo.domain.artist.domain.type.ActivityCategory;
import com.example.demo.domain.artist.domain.vo.ArtistName;
import com.example.demo.domain.artist.presentation.mapper.ArtistProfileMapper;
import java.time.Clock;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateArtistProfileService {

  private final ArtistUserQualificationRepository qualificationRepository;
  private final ArtistProfileRepository artistProfileRepository;
  private final AreaOfActivityRepository areaOfActivityRepository;
  private final ArtistProfileMapper artistProfileMapper;
  private final ArtistPermissionChecker permissionChecker;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;

  @Transactional
  public ArtistProfile execute(Long userId, CreateArtistProfileCommand command) {
    var qualification =
        qualificationRepository
            .findByUserId(userId)
            .orElseThrow(
                () -> new ArtistException(ArtistErrorCode.ARTIST_PROFILE_REQUIRES_VERIFIED_USER));

    permissionChecker.requireProfileCreationEligible(qualification);
    validateActivityFields(command.getActivityCategories());
    String artistName = ArtistName.of(command.getArtistName()).value();

    if (artistProfileRepository.findByUserId(userId).isPresent()) {
      throw new ArtistException(ArtistErrorCode.ARTIST_PROFILE_ALREADY_EXISTS);
    }

    // 일반적인 중복 요청은 DB까지 가지 않고 빠르게 예외 처리
    if (artistProfileRepository.existsByArtistName(artistName)) {
      throw new ArtistException(ArtistErrorCode.DUPLICATE_ARTIST_NAME);
    }

    ArtistProfile artistProfile;

    try {
      // 동시에 같은 작가명을 생성하는 요청이 들어올 경우
      // 두 요청 모두 existsByArtistName()을 통과할 수 있음
      // 이때 DB Unique Constraint 위반 발생
      // 해당 예외를 잡아서 500이 아닌 409 Conflict로 변환
      artistProfile =
          artistProfileRepository.save(
              artistProfileMapper.toEntity(
                  userId,
                  qualification.getSchoolEmail(),
                  qualification.getUniversityName(),
                  command));

    } catch (DataIntegrityViolationException e) {
      throw new ArtistException(ArtistErrorCode.DUPLICATE_ARTIST_NAME);
    }

    command
        .getActivityCategories()
        .forEach(
            category ->
                areaOfActivityRepository.save(AreaOfActivity.create(artistProfile, category)));

    eventPublisher.publishEvent(
        new ArtistProfileCreatedEvent(
            UUID.randomUUID(),
            artistProfile.getId(),
            userId,
            artistProfile.getArtistName(),
            1L,
            clock.instant()));

    return artistProfile;
  }

  private void validateActivityFields(List<ActivityCategory> activityCategories) {
    if (activityCategories == null
        || activityCategories.isEmpty()
        || activityCategories.size() > 2
        || new HashSet<>(activityCategories).size() != activityCategories.size()) {
      throw new ArtistException(ArtistErrorCode.INVALID_ACTIVITY_FIELDS);
    }
  }
}
