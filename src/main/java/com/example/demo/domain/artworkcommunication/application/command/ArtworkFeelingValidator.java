package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkFeeling;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtworkFeelingValidator {

  private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final CreatorExistenceRepository creatorExistenceRepository;

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

  public void validateNotArtworkCreator(Long displayArtworkId, Long userId) {
    if (creatorExistenceRepository
        .findCreatorNameByDisplayArtworkIdAndUserId(displayArtworkId, userId)
        .isPresent()) {
      throw new BusinessException(ArtworkCommunicationErrorCode.CREATOR_CANNOT_WRITE_FEELING);
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
