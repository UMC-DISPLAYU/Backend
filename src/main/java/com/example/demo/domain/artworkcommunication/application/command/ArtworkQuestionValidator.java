package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtworkQuestionValidator {

  private final ArtworkQuestionRepository artworkQuestionRepository;
  private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final CreatorExistenceRepository creatorExistenceRepository;
  private final ArtworkQuestionReplyRepository artworkQuestionReplyRepository;

  public ArtworkQuestion findQuestionOrThrow(Long questionId) {
    return artworkQuestionRepository
        .findById(questionId)
        .orElseThrow(() -> new BusinessException(ArtworkCommunicationErrorCode.QUESTION_NOT_FOUND));
  }

  public ArtworkQuestion findActiveQuestionForUpdateOrThrow(Long questionId) {
    return artworkQuestionRepository
        .findActiveByIdForUpdate(questionId)
        .orElseThrow(() -> new BusinessException(ArtworkCommunicationErrorCode.QUESTION_NOT_FOUND));
  }

  public void validateLikePermission(
      ArtworkQuestion artworkQuestion, Long displayArtworkId, Long userId) {
    if (Boolean.TRUE.equals(artworkQuestion.getIsPublic())
        || artworkQuestion.isWrittenBy(userId)
        || creatorExistenceRepository
            .findCreatorNameByDisplayArtworkIdAndUserId(displayArtworkId, userId)
            .isPresent()) {
      return;
    }

    throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_QUESTION_FORBIDDEN);
  }

  public void validateDisplayArtworkExists(Long displayArtworkId) {
    if (!displayArtworkExistenceRepository.existsById(displayArtworkId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_NOT_FOUND);
    }
  }

  public void validateUserExists(Long userId) {
    if (!userExistenceRepository.existsById(userId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
  }

  public void validateContent(String content) {
    if (content == null || content.isBlank()) {
      throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_QUESTION_CONTENT);
    }

    if (content.length() > 300) {
      throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_QUESTION_CONTENT);
    }
  }

  public void validateImages(List<ArtworkQuestion.ImageInfo> images) {
    validateImageValues(
        images == null
            ? null
            : images.stream()
                .map(
                    image ->
                        image == null
                            ? null
                            : new ImageValue(image.imageUrl(), image.width(), image.height()))
                .toList());
  }

  public void validateReplyImages(List<ArtworkQuestionReply.ImageInfo> images) {
    validateImageValues(
        images == null
            ? null
            : images.stream()
                .map(
                    image ->
                        image == null
                            ? null
                            : new ImageValue(image.imageUrl(), image.width(), image.height()))
                .toList());
  }

  public void validateQuestionTarget(ArtworkQuestion artworkQuestion, Long displayArtworkId) {
    if (artworkQuestion.isDeleted() || !artworkQuestion.belongsToArtwork(displayArtworkId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.QUESTION_NOT_FOUND);
    }
  }

  public void validateNotAnswered(ArtworkQuestion artworkQuestion) {
    if (artworkQuestion.isAnswered()) {
      throw new BusinessException(ArtworkCommunicationErrorCode.QUESTION_ALREADY_ANSWERED);
    }
  }

  public void validateWriter(ArtworkQuestion artworkQuestion, Long userId) {
    if (!artworkQuestion.isWrittenBy(userId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_QUESTION_FORBIDDEN);
    }
  }

  public ArtworkQuestionReply findActiveReplyForUpdateOrThrow(Long questionReplyId) {
    return artworkQuestionReplyRepository
        .findActiveByIdForUpdate(questionReplyId)
        .orElseThrow(
            () -> new BusinessException(ArtworkCommunicationErrorCode.QUESTION_REPLY_NOT_FOUND));
  }

  public void validateAccessibleReply(ArtworkQuestionReply reply, Long questionId, Long creatorId) {
    validateReplyTarget(reply, questionId);
    if (!reply.isWrittenBy(creatorId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_QUESTION_REPLY_FORBIDDEN);
    }
  }

  public void validateReplyTarget(ArtworkQuestionReply reply, Long questionId) {
    if (!reply.belongsToQuestion(questionId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.QUESTION_REPLY_NOT_FOUND);
    }
  }

  private void validateImageValues(List<ImageValue> images) {
    if (images == null || images.size() > 5) {
      throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_QUESTION_IMAGES);
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
      throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_QUESTION_IMAGES);
    }
  }

  private record ImageValue(String imageUrl, int width, int height) {}
}
