package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateArtworkQuestionService {

  private final ArtworkQuestionRepository artworkQuestionRepository;
  private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  private final UserExistenceRepository userExistenceRepository;

  public ArtworkQuestionResult updateQuestion(UpdateArtworkQuestionCommand command) {
    validateDisplayArtworkExists(command.displayArtworkId());
    validateUserExists(command.userId());
    validateContent(command.content());

    ArtworkQuestion artworkQuestion =
        artworkQuestionRepository
            .findById(command.questionId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.QUESTION_NOT_FOUND));

    validateNotDeleted(artworkQuestion);
    validateArtworkQuestionBelongsToArtwork(artworkQuestion, command.displayArtworkId());
    validateWriter(artworkQuestion, command.userId());

    artworkQuestion.update(command.content(), command.isPublic());

    ArtworkQuestion savedQuestion = artworkQuestionRepository.save(artworkQuestion);

    return new ArtworkQuestionResult(
        savedQuestion.getArtQueId(),
        savedQuestion.getContent(),
        savedQuestion.getIsPublic(),
        savedQuestion.getAnswerStatus(),
        savedQuestion.getCreatedAt(),
        savedQuestion.getUpdatedAt(),
        savedQuestion.getDeletedAt(),
        savedQuestion.getDisplayArtworkId(),
        savedQuestion.getUserId());
  }

  private void validateDisplayArtworkExists(Long displayArtworkId) {
    if (!displayArtworkExistenceRepository.existsById(displayArtworkId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_NOT_FOUND);
    }
  }

  private void validateUserExists(Long userId) {
    if (!userExistenceRepository.existsById(userId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.USER_NOT_FOUND);
    }
  }

  private void validateContent(String content) {
    if (content == null || content.isBlank()) {
      throw new BusinessException(ArtworkCommunicationErrorCode.INVALID_QUESTION_CONTENT);
    }
  }

  private void validateNotDeleted(ArtworkQuestion artworkQuestion) {
    if (artworkQuestion.isDeleted()) {
      throw new BusinessException(ArtworkCommunicationErrorCode.QUESTION_NOT_FOUND);
    }
  }

  private void validateArtworkQuestionBelongsToArtwork(
      ArtworkQuestion artworkQuestion, Long displayArtworkId) {
    if (!artworkQuestion.belongsToArtwork(displayArtworkId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.QUESTION_NOT_FOUND);
    }
  }

  private void validateWriter(ArtworkQuestion artworkQuestion, Long userId) {
    if (!artworkQuestion.isWrittenBy(userId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.ARTWORK_QUESTION_FORBIDDEN);
    }
  }
}
