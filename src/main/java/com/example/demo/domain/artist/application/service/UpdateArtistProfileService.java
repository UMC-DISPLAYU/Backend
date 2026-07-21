package com.example.demo.domain.artist.application.service;

import com.example.demo.domain.artist.application.command.UpdateArtistProfileCommand;
import com.example.demo.domain.artist.application.result.UpdateArtistProfileResult;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.domain.entity.AreaOfActivity;
import com.example.demo.domain.artist.domain.enums.ActivityCategory;
import com.example.demo.domain.artist.domain.repository.AreaOfActivityRepository;
import com.example.demo.domain.artist.domain.repository.ArtistProfileRepository;
import com.example.demo.domain.artist.exception.ArtistErrorCode;
import com.example.demo.domain.artist.exception.ArtistException;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.repository.UserRepository;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateArtistProfileService {

  private static final int INTRODUCTION_MAX_LENGTH = 100;

  private final UserRepository userRepository;
  private final ArtistProfileRepository artistProfileRepository;
  private final AreaOfActivityRepository areaOfActivityRepository;
  private final Clock clock;

  @Transactional
  public UpdateArtistProfileResult execute(UpdateArtistProfileCommand command) {
    User user =
        userRepository
            .findById(command.userId())
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

    if (!user.isVerified()) {
      throw new UserException(UserErrorCode.ARTIST_VERIFICATION_REQUIRED);
    }

    ArtistProfile profile =
        artistProfileRepository
            .findByUserId(command.userId())
            .orElseThrow(() -> new UserException(UserErrorCode.ARTIST_PROFILE_NOT_FOUND));

    Nickname nickname = validateNickname(command.nickname());
    String introduction = normalize(command.introduction());
    String externalLink = normalize(command.externalLink());
    String univName = normalize(command.univName());
    if (univName == null) {
      univName = profile.getUnivName();
    }
    validateIntroduction(introduction);
    validateActivityFields(command.fields());
    validateExternalLink(externalLink);

    try {
      if (!user.getNickname().equals(nickname.value())) {
        if (userRepository.existsByNickname(nickname.value())) {
          throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
        }
        user.changeNickname(nickname, LocalDateTime.now(clock));
        userRepository.flush();
      }

      user.changeUnivName(univName);
      profile.updateProfile(introduction, externalLink, univName);
      areaOfActivityRepository.deleteAllByArtistProfile(profile);
      command
          .fields()
          .forEach(field -> areaOfActivityRepository.save(AreaOfActivity.create(profile, field)));
    } catch (DataIntegrityViolationException exception) {
      throw new UserException(UserErrorCode.DUPLICATE_NICKNAME);
    }

    return new UpdateArtistProfileResult(
        nickname.value(), introduction, List.copyOf(command.fields()), externalLink, univName);
  }

  private Nickname validateNickname(String nickname) {
    if (nickname == null || nickname.isBlank()) {
      throw new UserException(UserErrorCode.MISSING_NICKNAME);
    }
    return Nickname.of(nickname);
  }

  private void validateIntroduction(String introduction) {
    if (introduction != null && introduction.length() > INTRODUCTION_MAX_LENGTH) {
      throw new ArtistException(ArtistErrorCode.INVALID_INTRODUCTION);
    }
  }

  private void validateActivityFields(List<ActivityCategory> fields) {
    if (fields == null
        || fields.isEmpty()
        || fields.size() > 2
        || fields.stream().anyMatch(field -> field == null)
        || new HashSet<>(fields).size() != fields.size()) {
      throw new ArtistException(ArtistErrorCode.INVALID_ACTIVITY_FIELDS);
    }
  }

  private void validateExternalLink(String externalLink) {
    if (externalLink == null) {
      return;
    }
    try {
      URI uri = new URI(externalLink);
      if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
          || uri.getHost() == null) {
        throw new ArtistException(ArtistErrorCode.INVALID_EXTERNAL_LINK);
      }
    } catch (URISyntaxException exception) {
      throw new ArtistException(ArtistErrorCode.INVALID_EXTERNAL_LINK);
    }
  }

  private String normalize(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}
