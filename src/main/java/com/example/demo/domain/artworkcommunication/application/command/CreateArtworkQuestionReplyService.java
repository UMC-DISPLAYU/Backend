package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.permission.ArtworkCommunicationPermissionChecker;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository.ContactCreator;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateArtworkQuestionReplyService {

  private final ArtworkQuestionReplyRepository artworkQuestionReplyRepository;
  private final ArtworkQuestionValidator artworkQuestionValidator;
  private final ArtworkCommunicationPermissionChecker permissionChecker;

  @Transactional
  public ArtworkQuestionReplyResult createQuestionReply(ArtworkQuestionReplyCommand command) {
    artworkQuestionValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkQuestionValidator.validateUserExists(command.userId());
    artworkQuestionValidator.validateContent(command.content());
    artworkQuestionValidator.validateReplyImages(command.images());

    ArtworkQuestion artworkQuestion =
        artworkQuestionValidator.findQuestionOrThrow(command.questionId());
    artworkQuestionValidator.validateQuestionTarget(artworkQuestion, command.displayArtworkId());
    artworkQuestionValidator.validateNotAnswered(artworkQuestion);

    ContactCreator contactCreator =
        permissionChecker.requireQnaHandler(command.displayArtworkId(), command.userId());

    ArtworkQuestionReply savedQuestionReply = saveQuestionReplyOrThrow(command, contactCreator);

    artworkQuestion.markAnswered();

    return new ArtworkQuestionReplyResult(
        savedQuestionReply.getQueReplyId(),
        savedQuestionReply.getContent(),
        savedQuestionReply.getCreatedAt(),
        savedQuestionReply.getQuestionId(),
        contactCreator.creatorId(),
        contactCreator.creatorName(),
        savedQuestionReply.getImages().stream()
            .map(
                image ->
                    new ArtworkQuestionReplyResult.ImageResult(
                        image.getQuestionReplyImageId(),
                        image.getImageUrl(),
                        image.getWidth(),
                        image.getHeight(),
                        image.getSortOrder()))
            .toList());
  }

  private ArtworkQuestionReply saveQuestionReplyOrThrow(
      ArtworkQuestionReplyCommand command, ContactCreator contactCreator) {
    try {
      return artworkQuestionReplyRepository.save(
          ArtworkQuestionReply.create(
              command.questionId(),
              command.content(),
              contactCreator.creatorId(),
              command.images()));
    } catch (DataIntegrityViolationException exception) {
      throw new BusinessException(ArtworkCommunicationErrorCode.QUESTION_ALREADY_ANSWERED);
    }
  }
}
