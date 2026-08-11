package com.example.demo.domain.personalartworkcommunication.application.permission;

import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonalArtworkCommunicationPermissionChecker {

  private final PersonalArtworkExistenceRepository personalArtworkExistenceRepository;

  public void requirePersonalArtworkOwner(Long personalArtworkId, Long userId) {
    if (!personalArtworkExistenceRepository.existsByIdAndUserId(personalArtworkId, userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_REPLY_FORBIDDEN);
    }
  }

  public void requirePersonalFeelingWriter(PersonalArtworkFeeling feeling, Long userId) {
    if (!feeling.isWrittenBy(userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_FEELING_FORBIDDEN);
    }
  }

  public void requirePersonalFeelingReplyWriter(PersonalArtworkFeelingReply reply, Long userId) {
    if (!reply.isWrittenBy(userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_FEELING_REPLY_FORBIDDEN);
    }
  }

  public void requirePersonalQuestionWriter(PersonalArtworkQuestion question, Long userId) {
    if (!question.isWrittenBy(userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_QUESTION_FORBIDDEN);
    }
  }

  public void requirePersonalQuestionAccessible(
      PersonalArtworkQuestion question, Long viewerUserId, boolean isOwner) {
    if (!question.isPublicQuestion() && !question.isWrittenBy(viewerUserId) && !isOwner) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_ARTWORK_QUESTION_FORBIDDEN);
    }
  }

  public void requirePersonalQuestionReplyWriter(PersonalArtworkQuestionReply reply, Long userId) {
    if (!reply.isWrittenBy(userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_REPLY_FORBIDDEN);
    }
  }
}
