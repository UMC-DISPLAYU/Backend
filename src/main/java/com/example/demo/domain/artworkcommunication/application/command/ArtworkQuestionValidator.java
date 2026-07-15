package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtworkQuestionValidator {

  private final ArtworkQuestionRepository artworkQuestionRepository;
  private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  private final UserExistenceRepository userExistenceRepository;

  public ArtworkQuestion findQuestionOrThrow(Long questionId) {
    return artworkQuestionRepository
        .findById(questionId)
        .orElseThrow(() -> new BusinessException(ArtworkCommunicationErrorCode.QUESTION_NOT_FOUND));
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
}
