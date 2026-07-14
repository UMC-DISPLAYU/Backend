package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository.ContactCreator;
import com.example.demo.domain.artworkcommunication.domain.repository.DisplayArtworkExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.UserExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateArtworkQuestionReplyService {

  private final ArtworkQuestionRepository artworkQuestionRepository;
  private final ArtworkQuestionReplyRepository artworkQuestionReplyRepository;
  private final DisplayArtworkExistenceRepository displayArtworkExistenceRepository;
  private final UserExistenceRepository userExistenceRepository;
  private final CreatorExistenceRepository creatorExistenceRepository;

  public ArtworkQuestionReplyResult createQuestionReply(ArtworkQuestionReplyCommand command) {
    validateDisplayArtworkExists(command.displayArtworkId());
    validateUserExists(command.userId());
    validateContent(command.content());

    ArtworkQuestion artworkQuestion = findQuestionOrThrow(command.questionId());
    validateQuestionTarget(artworkQuestion, command.displayArtworkId());
    validateNotAnswered(artworkQuestion);

    ContactCreator contactCreator =
        creatorExistenceRepository
            .findContactCreatorByDisplayArtworkIdAndUserId(
                command.displayArtworkId(), command.userId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.QNA_CONTACT_FORBIDDEN));

    ArtworkQuestionReply savedQuestionReply =
        artworkQuestionReplyRepository.save(
            ArtworkQuestionReply.create(command.questionId(), command.content()));

    artworkQuestion.markAnswered();

    return new ArtworkQuestionReplyResult(
        savedQuestionReply.getQueReplyId(),
        savedQuestionReply.getContent(),
        savedQuestionReply.getCreatedAt(),
        savedQuestionReply.getUpdatedAt(),
        savedQuestionReply.getDeletedAt(),
        savedQuestionReply.getArtQueId(),
        contactCreator.creatorId(),
        contactCreator.creatorName());
  }

  private ArtworkQuestion findQuestionOrThrow(Long questionId) {
    return artworkQuestionRepository
        .findById(questionId)
        .orElseThrow(() -> new BusinessException(ArtworkCommunicationErrorCode.QUESTION_NOT_FOUND));
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

  private void validateQuestionTarget(ArtworkQuestion artworkQuestion, Long displayArtworkId) {
    if (artworkQuestion.isDeleted() || !artworkQuestion.belongsToArtwork(displayArtworkId)) {
      throw new BusinessException(ArtworkCommunicationErrorCode.QUESTION_NOT_FOUND);
    }
  }

  private void validateNotAnswered(ArtworkQuestion artworkQuestion) {
    if (artworkQuestion.isAnswered()) {
      throw new BusinessException(ArtworkCommunicationErrorCode.QUESTION_ALREADY_ANSWERED);
    }
  }
}
