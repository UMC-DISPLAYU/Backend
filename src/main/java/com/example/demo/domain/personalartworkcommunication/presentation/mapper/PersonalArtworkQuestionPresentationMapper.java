package com.example.demo.domain.personalartworkcommunication.presentation.mapper;

import com.example.demo.domain.personalartworkcommunication.application.command.CreatePersonalArtworkQuestionCommand;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkQuestionResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionResponse;
import org.springframework.stereotype.Component;

@Component
public class PersonalArtworkQuestionPresentationMapper {

  public CreatePersonalArtworkQuestionCommand toCommand(
      Long personalArtworkId, Long userId, CreatePersonalArtworkQuestionRequest request) {

    boolean isPublic = request.isPublic() == null || request.isPublic();

    return new CreatePersonalArtworkQuestionCommand(
        personalArtworkId, userId, request.content(), isPublic);
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
}
