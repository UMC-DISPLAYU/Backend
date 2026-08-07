package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling.ImageInfo;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeelingReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkFeelingReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtworkFeelingValidator {

  private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final ArtworkFeelingReplyRepository artworkFeelingReplyRepository;

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
      throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_FEELING_CONTENT);
    }
    if (content.length() > 300) {
      throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_FEELING_CONTENT);
    }
  }

  public void validateImages(List<ImageInfo> images) {
    if (images == null || images.size() > 5) {
      throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_FEELING_IMAGES);
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
      throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_FEELING_IMAGES);
    }
  }

  public void validateReplyImages(List<ArtworkFeelingReply.ImageInfo> images) {
    if (images == null || images.size() > 5) {
      throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_FEELING_IMAGES);
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
      throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_FEELING_IMAGES);
    }
  }

  public void validateAccessibleFeeling(
      ArtworkFeeling artworkFeeling, Long displayArtworkId, Long userId) {
    validateNotDeleted(artworkFeeling);
    validateArtworkFeelingBelongsToArtwork(artworkFeeling, displayArtworkId);
    validateWriter(artworkFeeling, userId);
  }

  public void validateReplyTarget(ArtworkFeeling artworkFeeling, Long displayArtworkId) {
    validateNotDeleted(artworkFeeling);
    validateArtworkFeelingBelongsToArtwork(artworkFeeling, displayArtworkId);
  }

  public void validateReplyListTarget(ArtworkFeeling artworkFeeling, Long displayArtworkId) {
    validateArtworkFeelingBelongsToArtwork(artworkFeeling, displayArtworkId);
  }

  public void validateReplyDeletionTarget(ArtworkFeeling artworkFeeling, Long displayArtworkId) {
    validateArtworkFeelingBelongsToArtwork(artworkFeeling, displayArtworkId);
  }

  public ArtworkFeelingReply findReplyOrThrow(Long feelingReplyId) {
    ArtworkFeelingReply reply =
        artworkFeelingReplyRepository
            .findById(feelingReplyId)
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_REPLY_NOT_FOUND));
    if (reply.isDeleted()) {
      throw new BusinessException(ArtworkCommunicationErrorCode.FEELING_REPLY_NOT_FOUND);
    }
    return reply;
  }

  public ArtworkFeelingReply findActiveReplyForUpdateOrThrow(Long feelingReplyId) {
    return artworkFeelingReplyRepository
        .findActiveByIdForUpdate(feelingReplyId)
        .orElseThrow(
            () -> new BusinessException(ArtworkCommunicationErrorCode.FEELING_REPLY_NOT_FOUND));
  }

  public void validateReplyTarget(ArtworkFeelingReply reply, Long feelingId) {
    if (!reply.belongsToFeeling(feelingId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.FEELING_REPLY_NOT_FOUND);
    }
  }

  public void validateAccessibleReply(ArtworkFeelingReply reply, Long feelingId, Long userId) {
    validateReplyTarget(reply, feelingId);
    if (!reply.isWrittenBy(userId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_FEELING_REPLY_FORBIDDEN);
    }
  }

  private void validateNotDeleted(ArtworkFeeling artworkFeeling) {
    if (artworkFeeling.isDeleted()) {
      throw new BusinessException(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND);
    }
  }

  private void validateArtworkFeelingBelongsToArtwork(
      ArtworkFeeling artworkFeeling, Long displayArtworkId) {
    if (!artworkFeeling.belongsToArtwork(displayArtworkId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.FEELING_NOT_FOUND);
    }
  }

  private void validateWriter(ArtworkFeeling artworkFeeling, Long userId) {
    if (!artworkFeeling.isWrittenBy(userId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_FEELING_FORBIDDEN);
    }
  }
}
