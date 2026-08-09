package com.example.demo.domain.artist.application.service;

import com.example.demo.domain.artist.application.command.CreateArtistProfileCommand;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.entity.AreaOfActivity;
import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import com.example.demo.domain.artist.domain.error.ArtistErrorCode;
import com.example.demo.domain.artist.domain.error.ArtistException;
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.artist.presentation.mapper.ArtistProfileMapper;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.error.UserErrorCode;
import com.example.demo.domain.user.domain.error.UserException;
import com.example.demo.domain.user.domain.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateArtistProfileService {

  private final UserRepository userRepository;
  private final ArtistProfileRepository artistProfileRepository;
  private final AreaOfActivityRepository areaOfActivityRepository;
  private final ArtistProfileMapper artistProfileMapper;

  @Transactional
  public ArtistProfile execute(Long userId, CreateArtistProfileCommand command) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    validateVerifiedUser(user);
    validateActivityFields(command.getActivityCategories());

    if (artistProfileRepository.findByUser(user).isPresent()) {
      throw new ArtistException(ArtistErrorCode.ARTIST_PROFILE_ALREADY_EXISTS);
    }

    // 일반적인 중복 요청은 DB까지 가지 않고 빠르게 예외 처리
    if (artistProfileRepository.existsByArtistName(command.getArtistName())) {
      throw new ArtistException(ArtistErrorCode.DUPLICATE_ARTIST_NAME);
    }

    ArtistProfile artistProfile;

    try {
      // 동시에 같은 작가명을 생성하는 요청이 들어올 경우
      // 두 요청 모두 existsByArtistName()을 통과할 수 있음
      // 이때 DB Unique Constraint 위반 발생
      // 해당 예외를 잡아서 500이 아닌 409 Conflict로 변환
      artistProfile = artistProfileRepository.save(artistProfileMapper.toEntity(user, command));

    } catch (DataIntegrityViolationException e) {
      throw new ArtistException(ArtistErrorCode.DUPLICATE_ARTIST_NAME);
    }

    command
        .getActivityCategories()
        .forEach(
            category ->
                areaOfActivityRepository.save(AreaOfActivity.create(artistProfile, category)));

    return artistProfile;
  }

  private void validateVerifiedUser(User user) {
    if (!user.isVerified()
        || user.getSchoolEmail() == null
        || user.getSchoolEmail().isBlank()
        || user.getUnivName() == null
        || user.getUnivName().isBlank()) {
      throw new ArtistException(ArtistErrorCode.ARTIST_PROFILE_REQUIRES_VERIFIED_USER);
    }
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
