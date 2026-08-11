package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.permission.PersonalArtworkCommunicationPermissionChecker;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkQuestionReplyResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.domain.error.PersonalArtworkCommunicationErrorCode;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionReplyRepository;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkQuestionRepository;
import com.example.demo.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeletePersonalArtworkQuestionReplyService {

  private final PersonalArtworkQuestionRepository personalArtworkQuestionRepository;
  private final PersonalArtworkQuestionReplyRepository personalArtworkQuestionReplyRepository;
  private final PersonalArtworkQuestionValidator personalArtworkQuestionValidator;
  private final PersonalArtworkCommunicationPermissionChecker permissionChecker;

  @Transactional
  public DeletedPersonalArtworkQuestionReplyResult deleteReply(
      DeletePersonalArtworkQuestionReplyCommand command) {
    personalArtworkQuestionValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkQuestionValidator.validateUserExists(command.userId());
    permissionChecker.requirePersonalArtworkOwner(command.personalArtworkId(), command.userId());

    PersonalArtworkQuestion question =
        personalArtworkQuestionRepository
            .findActiveById(command.personalQuestionId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        PersonalArtworkCommunicationErrorCode.PERSONAL_QUESTION_NOT_FOUND));
    personalArtworkQuestionValidator.validateQuestionTarget(question, command.personalArtworkId());

    PersonalArtworkQuestionReply reply =
        personalArtworkQuestionValidator.findActiveReplyForUpdateOrThrow(
            command.personalQuestionReplyId());
    personalArtworkQuestionValidator.validateReplyBelongsToQuestion(
        reply, command.personalQuestionId());
    permissionChecker.requirePersonalQuestionReplyWriter(reply, command.userId());

    reply.delete();
    PersonalArtworkQuestionReply savedReply = personalArtworkQuestionReplyRepository.save(reply);
    question.markWaiting();
    personalArtworkQuestionRepository.save(question);

    return new DeletedPersonalArtworkQuestionReplyResult(
        savedReply.getPersonalQuestionReplyId(), savedReply.getDeletedAt());
  }
}
