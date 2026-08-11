package com.example.demo.domain.personalartworkcommunication.application.command;

import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkFeelingReplyResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeeling;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkFeelingReply;
import com.example.demo.domain.personalartworkcommunication.domain.repository.PersonalArtworkFeelingReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeletePersonalArtworkFeelingReplyService {

  private final PersonalArtworkFeelingReplyRepository personalArtworkFeelingReplyRepository;
  private final PersonalArtworkFeelingValidator personalArtworkFeelingValidator;

  public DeletedPersonalArtworkFeelingReplyResult deleteReply(
      DeletePersonalArtworkFeelingReplyCommand command) {
    personalArtworkFeelingValidator.validatePersonalArtworkExists(command.personalArtworkId());
    personalArtworkFeelingValidator.validateUserExists(command.userId());

    PersonalArtworkFeeling feeling =
        personalArtworkFeelingValidator.findFeelingOrThrow(command.personalFeelingId());
    personalArtworkFeelingValidator.validateReplyDeletionTarget(
        feeling, command.personalArtworkId());

    PersonalArtworkFeelingReply reply =
        personalArtworkFeelingValidator.findActiveReplyForUpdateOrThrow(
            command.personalFeelingReplyId());
    personalArtworkFeelingValidator.validateAccessibleReply(
        reply, command.personalFeelingId(), command.userId());

    reply.delete();
    PersonalArtworkFeelingReply saved = personalArtworkFeelingReplyRepository.save(reply);

    return new DeletedPersonalArtworkFeelingReplyResult(
        saved.getPersonalFeelingReplyId(), saved.getDeletedAt());
  }
}
