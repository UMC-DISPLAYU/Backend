package com.example.demo.domain.artist.application.service;

import com.example.demo.domain.artist.application.command.CreateArtistProfileCommand;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.entity.AreaOfActivity;
import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.artist.exception.ArtistErrorCode;
import com.example.demo.domain.artist.exception.ArtistException;
import com.example.demo.domain.artist.presentation.mapper.ArtistProfileMapper;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    if (artistProfileRepository.existsByArtistName(command.getArtistName())) {
      throw new ArtistException(ArtistErrorCode.DUPLICATE_ARTIST_NAME);
    }

    ArtistProfile artistProfile =
        artistProfileRepository.save(artistProfileMapper.toEntity(user, command));
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
