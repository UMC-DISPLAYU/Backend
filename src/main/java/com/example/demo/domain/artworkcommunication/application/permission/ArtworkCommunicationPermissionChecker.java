package com.example.demo.domain.artworkcommunication.application.permission;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository.ContactCreator;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtworkCommunicationPermissionChecker {

  private final CreatorExistenceRepository creatorExistenceRepository;

  public void requireFeelingWriter(ArtworkFeeling feeling, Long userId) {
    if (!feeling.isWrittenBy(userId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_FEELING_FORBIDDEN);
    }
  }

  public void requireFeelingReplyWriter(ArtworkFeelingReply reply, Long userId) {
    if (!reply.isWrittenBy(userId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_FEELING_REPLY_FORBIDDEN);
    }
  }

  public void requireQuestionWriter(ArtworkQuestion question, Long userId) {
    if (!question.isWrittenBy(userId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_QUESTION_FORBIDDEN);
    }
  }

  public void requireQuestionAccessible(
      ArtworkQuestion question, Long viewerUserId, boolean isCreatorOrHandler) {
    if (!Boolean.TRUE.equals(question.getIsPublic())
        && !question.isWrittenBy(viewerUserId)
        && !isCreatorOrHandler) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_QUESTION_FORBIDDEN);
    }
  }

  public void requireQuestionReplyWriter(ArtworkQuestionReply reply, Long creatorId) {
    if (!reply.isWrittenBy(creatorId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_QUESTION_REPLY_FORBIDDEN);
    }
  }

  public ContactCreator requireQnaHandler(Long displayArtworkId, Long userId) {
    return creatorExistenceRepository
        .findContactCreatorByDisplayArtworkIdAndUserId(displayArtworkId, userId)
        .orElseThrow(
            () -> new BusinessException(ArtworkCommunicationErrorCode.QNA_CONTACT_FORBIDDEN));
  }
}
