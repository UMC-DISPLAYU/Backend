package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonalArtworkQuestionValidator {

  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final PersonalArtworkQuestionReplyRepository personalArtworkQuestionReplyRepository;

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
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.INVALID_QUESTION_CONTENT);
    }
    if (content.length() > 300) {
      throw new BusinessException(PersonalArtworkCommunicationErrorCode.INVALID_QUESTION_CONTENT);
    }
  }

  public void validateNotPersonalArtworkCreator(Long personalArtworkId, Long userId) {
    if (personalArtworkExistenceRepository.existsByIdAndUserId(personalArtworkId, userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.CREATOR_CANNOT_WRITE_QUESTION);
    }
  }

  public void validatePersonalArtworkCreator(Long personalArtworkId, Long userId) {
    if (!personalArtworkExistenceRepository.existsByIdAndUserId(personalArtworkId, userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_REPLY_FORBIDDEN);
    }
  }

  public void validateAccessiblePersonalQuestion(
      PersonalArtworkQuestion personalArtworkQuestion, Long personalArtworkId, Long userId) {
    validateNotDeleted(personalArtworkQuestion);
    validatePersonalArtworkQuestionBelongsToPersonalArtwork(
        personalArtworkQuestion, personalArtworkId);
    validateWriter(personalArtworkQuestion, userId);
  }

  public void validateReplyTarget(
      PersonalArtworkQuestion personalArtworkQuestion, Long personalArtworkId) {
    validateNotDeleted(personalArtworkQuestion);
    validatePersonalArtworkQuestionBelongsToPersonalArtwork(
        personalArtworkQuestion, personalArtworkId);
  }

  public PersonalArtworkQuestionReply findActiveReplyForUpdateOrThrow(
      Long personalQuestionReplyId) {
    return personalArtworkQuestionReplyRepository
        .findActiveByIdForUpdate(personalQuestionReplyId)
        .orElseThrow(
            () ->
                new BusinessException(
                    PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_REPLY_NOT_FOUND));
  }

  public void validateAccessibleReply(
      PersonalArtworkQuestionReply reply, Long personalQuestionId, Long userId) {
    if (!reply.belongsToQuestion(personalQuestionId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_REPLY_NOT_FOUND);
    }
    if (!reply.isWrittenBy(userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_REPLY_FORBIDDEN);
    }
  }

  private void validateNotDeleted(PersonalArtworkQuestion personalArtworkQuestion) {
    if (personalArtworkQuestion.isDeleted()) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_NOT_FOUND);
    }
  }

  private void validatePersonalArtworkQuestionBelongsToPersonalArtwork(
      PersonalArtworkQuestion personalArtworkQuestion, Long personalArtworkId) {
    if (!personalArtworkQuestion.belongsToArtwork(personalArtworkId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_NOT_FOUND);
    }
  }

  private void validateWriter(PersonalArtworkQuestion personalArtworkQuestion, Long userId) {
    if (!personalArtworkQuestion.isWrittenBy(userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_QUESTION_FORBIDDEN);
    }
  }
}
