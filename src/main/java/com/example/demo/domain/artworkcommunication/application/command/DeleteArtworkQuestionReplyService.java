package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.result.DeletedArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.error.ArtworkCommunicationErrorCode;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.CreatorExistenceRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteArtworkQuestionReplyService {

  private final ArtworkQuestionRepository artworkQuestionRepository;
  private final ArtworkQuestionReplyRepository artworkQuestionReplyRepository;
  private final CreatorExistenceRepository creatorExistenceRepository;
  private final ArtworkQuestionValidator artworkQuestionValidator;

  public DeletedArtworkQuestionReplyResult deleteReply(DeleteArtworkQuestionReplyCommand command) {
    artworkQuestionValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkQuestionValidator.validateUserExists(command.userId());

    ArtworkQuestion question = artworkQuestionValidator.findQuestionOrThrow(command.questionId());
    artworkQuestionValidator.validateQuestionTarget(question, command.displayArtworkId());

    Long creatorId =
        creatorExistenceRepository
            .findContactCreatorByDisplayArtworkIdAndUserId(
                command.displayArtworkId(), command.userId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        ArtworkCommunicationErrorCode.ARTWORK_QUESTION_REPLY_FORBIDDEN))
            .creatorId();

    ArtworkQuestionReply reply =
        artworkQuestionValidator.findActiveReplyForUpdateOrThrow(command.questionReplyId());
    artworkQuestionValidator.validateAccessibleReply(reply, command.questionId(), creatorId);

    reply.delete();
    ArtworkQuestionReply savedReply = artworkQuestionReplyRepository.save(reply);
    question.markWaiting();
    artworkQuestionRepository.save(question);

    return new DeletedArtworkQuestionReplyResult(
        savedReply.getQueReplyId(), savedReply.getDeletedAt());
  }
}
