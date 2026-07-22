package com.example.demo.domain.personalartworkcommunication.presentation.mapper;

import com.example.demo.domain.personalartworkcommunication.application.command.CreatePersonalArtworkFeelingCommand;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkFeelingResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingResponse;
import org.springframework.stereotype.Component;

@Component
public class PersonalArtworkFeelingPresentationMapper {

  public CreatePersonalArtworkFeelingCommand toCommand(
      Long personalArtworkId, Long userId, CreatePersonalArtworkFeelingRequest request) {
    return new CreatePersonalArtworkFeelingCommand(personalArtworkId, userId, request.content());
  }

  public PersonalArtworkFeelingResponse toResponse(PersonalArtworkFeelingResult result) {
    return new PersonalArtworkFeelingResponse(
        result.personalFeelingId(), result.userId(), result.content(), result.createdAt());
  }

  public DeletedPersonalArtworkFeelingResponse toResponse(
      DeletedPersonalArtworkFeelingResult result) {
    return new DeletedPersonalArtworkFeelingResponse(
        result.personalFeelingId(), result.deletedAt());
  }
}
