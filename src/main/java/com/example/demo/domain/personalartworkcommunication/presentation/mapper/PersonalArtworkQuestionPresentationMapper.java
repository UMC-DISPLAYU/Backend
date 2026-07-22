package com.example.demo.domain.personalartworkcommunication.presentation.mapper;

import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkQuestionCommand;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkQuestionReplyCommand;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionReplyResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkQuestionResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionResponse;
import org.springframework.stereotype.Component;

@Component
public class PersonalArtworkQuestionPresentationMapper {

  public PersonalArtworkQuestionCommand toCommand(
      Long personalArtworkId, Long userId, CreatePersonalArtworkQuestionRequest request) {

    boolean isPublic = request.isPublic() == null || request.isPublic();

    return new PersonalArtworkQuestionCommand(
        personalArtworkId, userId, request.content(), isPublic);
  }

  public PersonalArtworkQuestionReplyCommand toCommand(
      Long personalArtworkId,
      Long personalQuestionId,
      Long userId,
      CreatePersonalArtworkQuestionReplyRequest request) {
    return new PersonalArtworkQuestionReplyCommand(
        personalArtworkId, personalQuestionId, userId, request.content());
  }

  public PersonalArtworkQuestionResponse toResponse(PersonalArtworkQuestionResult result) {
    return new PersonalArtworkQuestionResponse(
        result.personalQuestionId(),
        result.content(),
        result.isPublic(),
        result.answerStatus(),
        result.createdAt(),
        result.userId());
  }

  public DeletedPersonalArtworkQuestionResponse toResponse(
      DeletedPersonalArtworkQuestionResult result) {
    return new DeletedPersonalArtworkQuestionResponse(
        result.personalQuestionId(), result.deletedAt());
  }

  public PersonalArtworkQuestionReplyResponse toResponse(
      PersonalArtworkQuestionReplyResult result) {
    return new PersonalArtworkQuestionReplyResponse(
        result.personalQuestionReplyId(),
        result.createdAt(),
        result.content(),
        result.personalQuestionId(),
        result.userId(),
        result.nickname(),
        result.isCreator());
  }
}
