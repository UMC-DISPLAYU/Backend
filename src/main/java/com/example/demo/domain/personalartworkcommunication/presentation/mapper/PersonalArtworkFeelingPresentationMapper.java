package com.example.demo.domain.personalartworkcommunication.presentation.mapper;

import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingCommand;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingReplyCommand;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkFeelingResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingResponse;
import org.springframework.stereotype.Component;

@Component
public class PersonalArtworkFeelingPresentationMapper {

  public PersonalArtworkFeelingCommand toCommand(
      Long personalArtworkId, Long userId, CreatePersonalArtworkFeelingRequest request) {
    return new PersonalArtworkFeelingCommand(personalArtworkId, userId, request.content());
  }

  public PersonalArtworkFeelingReplyCommand toCommand(
      Long personalArtworkId,
      Long personalFeelingId,
      Long userId,
      CreatePersonalArtworkFeelingReplyRequest request) {
    return new PersonalArtworkFeelingReplyCommand(
        personalArtworkId, personalFeelingId, userId, request.content());
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

  public PersonalArtworkFeelingReplyResponse toResponse(PersonalArtworkFeelingReplyResult result) {
    return new PersonalArtworkFeelingReplyResponse(
        result.personalFeelingReplyId(),
        result.createdAt(),
        result.content(),
        result.personalFeelingId(),
        result.userId(),
        result.nickname(),
        result.isCreator());
  }
}
