package com.example.demo.domain.personalartworkcommunication.presentation.mapper;

import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingCommand;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkFeelingReplyCommand;
import com.example.demo.domain.personalartworkcommunication.application.query.GetPersonalArtworkFeelingsQuery;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkFeelingReplyResult;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingLikeResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult.PersonalArtworkFeelingItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingListResult.PersonalArtworkFeelingReplyItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyLikeResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingReplyResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkFeelingResult;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkFeelingReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkFeelingResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingLikeResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingListResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingListResponse.PersonalArtworkFeelingItemResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingListResponse.PersonalArtworkFeelingReplyItemResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingListResponse.PersonalArtworkFeelingUserResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingReplyLikeResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingResponse;
import org.springframework.stereotype.Component;

@Component
public class PersonalArtworkFeelingPresentationMapper {

  public GetPersonalArtworkFeelingsQuery toQuery(Long personalArtworkId, Long cursorId) {
    return new GetPersonalArtworkFeelingsQuery(personalArtworkId, cursorId);
  }

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

  public DeletedPersonalArtworkFeelingReplyResponse toResponse(
      DeletedPersonalArtworkFeelingReplyResult result) {
    return new DeletedPersonalArtworkFeelingReplyResponse(
        result.personalFeelingReplyId(), result.deletedAt());
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

  public PersonalArtworkFeelingLikeResponse toResponse(PersonalArtworkFeelingLikeResult result) {
    return new PersonalArtworkFeelingLikeResponse(
        result.personalFeelingId(),
        result.liked(),
        result.likeCount(),
        result.createdAt(),
        result.deletedAt());
  }

  public PersonalArtworkFeelingReplyLikeResponse toResponse(
      PersonalArtworkFeelingReplyLikeResult result) {
    return new PersonalArtworkFeelingReplyLikeResponse(
        result.personalFeelingReplyId(),
        result.liked(),
        result.likeCount(),
        result.createdAt(),
        result.deletedAt());
  }

  public PersonalArtworkFeelingListResponse toResponse(PersonalArtworkFeelingListResult result) {
    return new PersonalArtworkFeelingListResponse(
        result.feelings().stream().map(this::toFeelingItemResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  private PersonalArtworkFeelingItemResponse toFeelingItemResponse(
      PersonalArtworkFeelingItemResult result) {
    PersonalArtworkFeelingUserResponse user =
        new PersonalArtworkFeelingUserResponse(
            result.user().userId(), result.user().nickname(), result.user().isCreator());
    return new PersonalArtworkFeelingItemResponse(
        result.personalFeelingId(),
        result.content(),
        result.createdAt(),
        user,
        result.replies().stream().map(this::toReplyItemResponse).toList());
  }

  private PersonalArtworkFeelingReplyItemResponse toReplyItemResponse(
      PersonalArtworkFeelingReplyItemResult result) {
    return new PersonalArtworkFeelingReplyItemResponse(
        result.personalFeelingReplyId(),
        result.userId(),
        result.nickname(),
        result.content(),
        result.createdAt(),
        result.isCreator());
  }
}
