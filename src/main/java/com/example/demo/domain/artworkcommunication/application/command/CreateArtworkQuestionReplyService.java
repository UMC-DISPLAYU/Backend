package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository.ContactCreator;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateArtworkQuestionReplyService {

  private final ArtworkQuestionReplyRepository artworkQuestionReplyRepository;
  private final CreatorExistenceRepository creatorExistenceRepository;
  private final ArtworkQuestionValidator artworkQuestionValidator;

  public ArtworkQuestionReplyResult createQuestionReply(ArtworkQuestionReplyCommand command) {
    artworkQuestionValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkQuestionValidator.validateUserExists(command.userId());
    artworkQuestionValidator.validateContent(command.content());

    ArtworkQuestion artworkQuestion =
        artworkQuestionValidator.findQuestionOrThrow(command.questionId());
    artworkQuestionValidator.validateQuestionTarget(artworkQuestion, command.displayArtworkId());
    artworkQuestionValidator.validateNotAnswered(artworkQuestion);

    ContactCreator contactCreator =
        creatorExistenceRepository
            .findContactCreatorByDisplayArtworkIdAndUserId(
                command.displayArtworkId(), command.userId())
            .orElseThrow(
                () -> new BusinessException(ArtworkCommunicationErrorCode.QNA_CONTACT_FORBIDDEN));

    ArtworkQuestionReply savedQuestionReply = saveQuestionReplyOrThrow(command, contactCreator);

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

  private ArtworkQuestionReply saveQuestionReplyOrThrow(
      ArtworkQuestionReplyCommand command, ContactCreator contactCreator) {
    try {
      return artworkQuestionReplyRepository.save(
          ArtworkQuestionReply.create(
              command.questionId(), command.content(), contactCreator.creatorId()));
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(ArtworkCommunicationErrorCode.QUESTION_ALREADY_ANSWERED);
    }
  }
}
