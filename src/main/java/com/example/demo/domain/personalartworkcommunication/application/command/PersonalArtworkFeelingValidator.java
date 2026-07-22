package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonalArtworkFeelingValidator {

  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  private final UserExistenceRepository userExistenceRepository;

  public void validatePersonalArtworkExists(Long personalArtworkId) {
    if (!personalArtworkExistenceRepository.existsById(personalArtworkId)) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_NOT_FOUND);
    }
  }

  public void validateUserExists(Long userId) {
    if (!userExistenceRepository.existsById(userId)) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
  }

  public void validateContent(String content) {
    if (content == null || content.isBlank()) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.INVALID_FEELING_CONTENT);
    }
    if (content.length() > 300) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.INVALID_FEELING_CONTENT);
    }
  }

  public void validateNotPersonalArtworkCreator(Long personalArtworkId, Long userId) {
    if (personalArtworkExistenceRepository.existsByIdAndUserId(personalArtworkId, userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.CREATOR_CANNOT_WRITE_FEELING);
    }
  }

  public void validateAccessiblePersonalFeeling(
      PersonalArtworkFeeling personalArtworkFeeling, Long personalArtworkId, Long userId) {
    validateNotDeleted(personalArtworkFeeling);
    validatePersonalArtworkFeelingBelongsToPersonalArtwork(
        personalArtworkFeeling, personalArtworkId);
    validateWriter(personalArtworkFeeling, userId);
  }

  private void validateNotDeleted(PersonalArtworkFeeling personalArtworkFeeling) {
    if (personalArtworkFeeling.isDeleted()) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.PERSONAL_FEELING_NOT_FOUND);
    }
  }

  private void validatePersonalArtworkFeelingBelongsToPersonalArtwork(
      PersonalArtworkFeeling personalArtworkFeeling, Long personalArtworkId) {
    if (!personalArtworkFeeling.belongsToArtwork(personalArtworkId)) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.PERSONAL_FEELING_NOT_FOUND);
    }
  }

  private void validateWriter(PersonalArtworkFeeling personalArtworkFeeling, Long userId) {
    if (!personalArtworkFeeling.isWrittenBy(userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_FEELING_FORBIDDEN);
    }
  }
}
