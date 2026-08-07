package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling.ImageInfo;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonalArtworkFeelingValidator {

  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  private final PersonalArtworkFeelingRepository personalArtworkFeelingRepository;
  private final PersonalArtworkFeelingReplyRepository personalArtworkFeelingReplyRepository;
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

  public void validateImages(List<ImageInfo> images) {
    if (images == null || images.size() > 5) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.INVALID_FEELING_IMAGES);
    }
    if (images.stream()
        .anyMatch(
            image ->
                image == null
                    || image.imageUrl() == null
                    || image.imageUrl().isBlank()
                    || image.imageUrl().length() > 2048
                    || image.width() <= 0
                    || image.height() <= 0)) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.INVALID_FEELING_IMAGES);
    }
  }

  public void validateReplyImages(List<PersonalArtworkFeelingReply.ImageInfo> images) {
    if (images == null || images.size() > 5) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.INVALID_FEELING_IMAGES);
    }
    if (images.stream()
        .anyMatch(
            image ->
                image == null
                    || image.imageUrl() == null
                    || image.imageUrl().isBlank()
                    || image.imageUrl().length() > 2048
                    || image.width() <= 0
                    || image.height() <= 0)) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.INVALID_FEELING_IMAGES);
    }
  }

  public void validateAccessiblePersonalFeeling(
      PersonalArtworkFeeling personalArtworkFeeling, Long personalArtworkId, Long userId) {
    validateNotDeleted(personalArtworkFeeling);
    validatePersonalArtworkFeelingBelongsToPersonalArtwork(
        personalArtworkFeeling, personalArtworkId);
    validateWriter(personalArtworkFeeling, userId);
  }

  public void validateReplyTarget(
      PersonalArtworkFeeling personalArtworkFeeling, Long personalArtworkId) {
    validateNotDeleted(personalArtworkFeeling);
    validatePersonalArtworkFeelingBelongsToPersonalArtwork(
        personalArtworkFeeling, personalArtworkId);
  }

  public void validateReplyListTarget(
      PersonalArtworkFeeling personalArtworkFeeling, Long personalArtworkId) {
    validatePersonalArtworkFeelingBelongsToPersonalArtwork(
        personalArtworkFeeling, personalArtworkId);
  }

  public PersonalArtworkFeeling findFeelingOrThrow(Long personalFeelingId) {
    return personalArtworkFeelingRepository
        .findById(personalFeelingId)
        .orElseThrow(
            () ->
                new BusinessException(
                    PersonalArtworkCommunicationErrorCode.PERSONAL_FEELING_NOT_FOUND));
  }

  public PersonalArtworkFeelingReply findReplyOrThrow(Long personalFeelingReplyId) {
    PersonalArtworkFeelingReply reply =
        personalArtworkFeelingReplyRepository
            .findById(personalFeelingReplyId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_FEELING_REPLY_NOT_FOUND));
    if (reply.isDeleted()) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_FEELING_REPLY_NOT_FOUND);
    }
    return reply;
  }

  public PersonalArtworkFeelingReply findActiveReplyForUpdateOrThrow(Long personalFeelingReplyId) {
    return personalArtworkFeelingReplyRepository
        .findActiveByIdForUpdate(personalFeelingReplyId)
        .orElseThrow(
            () ->
                new BusinessException(
                    PersonalArtworkCommunicationErrorCode.PERSONAL_FEELING_REPLY_NOT_FOUND));
  }

  public void validateReplyTarget(PersonalArtworkFeelingReply reply, Long personalFeelingId) {
    if (!reply.belongsToFeeling(personalFeelingId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_FEELING_REPLY_NOT_FOUND);
    }
  }

  public void validateAccessibleReply(
      PersonalArtworkFeelingReply reply, Long personalFeelingId, Long userId) {
    validateReplyTarget(reply, personalFeelingId);
    if (!reply.isWrittenBy(userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_FEELING_REPLY_FORBIDDEN);
    }
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
