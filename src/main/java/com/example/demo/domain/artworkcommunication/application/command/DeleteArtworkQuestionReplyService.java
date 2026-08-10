package com.example.demo.domain.artworkcommunication.application.command;

import com.example.demo.domain.artworkcommunication.application.permission.ArtworkCommunicationPermissionChecker;
import com.example.demo.domain.artworkcommunication.application.result.DeletedArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionReplyRepository;
import com.example.demo.domain.artworkcommunication.domain.repository.ArtworkQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteArtworkQuestionReplyService {

  private final ArtworkQuestionRepository artworkQuestionRepository;
  private final ArtworkQuestionReplyRepository artworkQuestionReplyRepository;
  private final ArtworkQuestionValidator artworkQuestionValidator;
  private final ArtworkCommunicationPermissionChecker permissionChecker;

  public DeletedArtworkQuestionReplyResult deleteReply(DeleteArtworkQuestionReplyCommand command) {
    artworkQuestionValidator.validateDisplayArtworkExists(command.displayArtworkId());
    artworkQuestionValidator.validateUserExists(command.userId());

    ArtworkQuestion question = artworkQuestionValidator.findQuestionOrThrow(command.questionId());
    artworkQuestionValidator.validateQuestionTarget(question, command.displayArtworkId());

    Long creatorId =
        permissionChecker
            .requireQnaHandler(command.displayArtworkId(), command.userId())
            .creatorId();

    ArtworkQuestionReply reply =
        artworkQuestionValidator.findActiveReplyForUpdateOrThrow(command.questionReplyId());
    artworkQuestionValidator.validateReplyTarget(reply, command.questionId());
    permissionChecker.requireQuestionReplyWriter(reply, creatorId);

    reply.delete();
    ArtworkQuestionReply savedReply = artworkQuestionReplyRepository.save(reply);
    question.markWaiting();
    artworkQuestionRepository.save(question);

    return new DeletedArtworkQuestionReplyResult(
        savedReply.getQueReplyId(), savedReply.getDeletedAt());
  }
}
