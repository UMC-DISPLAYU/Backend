package com.example.demo.domain.personalartworkcommunication.application.permission;

import com.example.demo.domain.personalartwork.application.usecase.GetPersonalArtworkAccessUseCase;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonalArtworkCommunicationPermissionChecker {

  private final GetPersonalArtworkAccessUseCase getPersonalArtworkAccessUseCase;

  public void requirePersonalArtworkOwner(Long personalArtworkId, Long userId) {
    if (!isPersonalArtworkOwner(personalArtworkId, userId)) {
      throw new BusinessException(
          PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_REPLY_FORBIDDEN);
    }
  }

  public boolean isPersonalArtworkOwner(Long personalArtworkId, Long userId) {
    return getPersonalArtworkAccessUseCase
        .getPersonalArtworkAccess(personalArtworkId)
        .map(access -> access.isOwner(userId))
        .orElse(false);
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
