package com.example.demo.domain.personalartworkcommunication.presentation.mapper;

import com.example.demo.domain.personalartworkcommunication.application.command.DeletePersonalArtworkQuestionCommand;
import com.example.demo.domain.personalartworkcommunication.application.command.DeletePersonalArtworkQuestionReplyCommand;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkQuestionCommand;
import com.example.demo.domain.personalartworkcommunication.application.command.PersonalArtworkQuestionReplyCommand;
import com.example.demo.domain.personalartworkcommunication.application.query.GetPersonalArtworkQuestionsQuery;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkQuestionReplyResult;
import com.example.demo.domain.personalartworkcommunication.application.result.DeletedPersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult.PersonalArtworkQuestionItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionListResult.PersonalArtworkQuestionReplyItemResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionReplyResult;
import com.example.demo.domain.personalartworkcommunication.application.result.PersonalArtworkQuestionResult;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestion;
import com.example.demo.domain.personalartworkcommunication.domain.aggregate.PersonalArtworkQuestionReply;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkQuestionReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkQuestionResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionListResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionListResponse.PersonalArtworkQuestionItemResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionListResponse.PersonalArtworkQuestionReplyItemResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionListResponse.PersonalArtworkQuestionUserResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionListResponse.QuestionImageResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionListResponse.ReplyImageResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PersonalArtworkQuestionPresentationMapper {

  public GetPersonalArtworkQuestionsQuery toQuery(
      Long personalArtworkId, Long cursorId, int size, Long userId) {
    return new GetPersonalArtworkQuestionsQuery(personalArtworkId, cursorId, size, userId);
  }

  public DeletePersonalArtworkQuestionCommand toCommand(
      Long personalArtworkId, Long personalQuestionId, Long userId) {
    return new DeletePersonalArtworkQuestionCommand(personalArtworkId, personalQuestionId, userId);
  }

  public DeletePersonalArtworkQuestionReplyCommand toReplyDeleteCommand(
      Long personalArtworkId, Long personalQuestionId, Long personalQuestionReplyId, Long userId) {
    return new DeletePersonalArtworkQuestionReplyCommand(
        personalArtworkId, personalQuestionId, personalQuestionReplyId, userId);
  }

  public PersonalArtworkQuestionCommand toCommand(
      Long personalArtworkId, Long userId, CreatePersonalArtworkQuestionRequest request) {

    boolean isPublic = request.isPublic() == null || request.isPublic();
    List<PersonalArtworkQuestion.ImageInfo> images =
        request.images() == null
            ? List.of()
            : request.images().stream()
                .map(
                    image ->
                        image == null
                            ? null
                            : new PersonalArtworkQuestion.ImageInfo(
                                image.imageUrl(), image.width(), image.height()))
                .toList();

    return new PersonalArtworkQuestionCommand(
        personalArtworkId, userId, request.content(), isPublic, images);
  }

  public PersonalArtworkQuestionReplyCommand toCommand(
      Long personalArtworkId,
      Long personalQuestionId,
      Long userId,
      CreatePersonalArtworkQuestionReplyRequest request) {
    List<PersonalArtworkQuestionReply.ImageInfo> images =
        request.images() == null
            ? List.of()
            : request.images().stream()
                .map(
                    image ->
                        image == null
                            ? null
                            : new PersonalArtworkQuestionReply.ImageInfo(
                                image.imageUrl(), image.width(), image.height()))
                .toList();
    return new PersonalArtworkQuestionReplyCommand(
        personalArtworkId, personalQuestionId, userId, request.content(), images);
  }

  public PersonalArtworkQuestionResponse toResponse(PersonalArtworkQuestionResult result) {
    return new PersonalArtworkQuestionResponse(
        result.personalQuestionId(),
        result.content(),
        result.isPublic(),
        result.answerStatus(),
        result.createdAt(),
        result.userId(),
        result.images().stream()
            .map(
                image ->
                    new PersonalArtworkQuestionResponse.ImageResponse(
                        image.personalQuestionImageId(),
                        image.imageUrl(),
                        image.width(),
                        image.height(),
                        image.sortOrder()))
            .toList());
  }

  public DeletedPersonalArtworkQuestionResponse toResponse(
      DeletedPersonalArtworkQuestionResult result) {
    return new DeletedPersonalArtworkQuestionResponse(
        result.personalQuestionId(), result.deletedAt());
  }

  public DeletedPersonalArtworkQuestionReplyResponse toResponse(
      DeletedPersonalArtworkQuestionReplyResult result) {
    return new DeletedPersonalArtworkQuestionReplyResponse(
        result.personalQuestionReplyId(), result.deletedAt());
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
        result.isCreator(),
        result.images().stream()
            .map(
                image ->
                    new PersonalArtworkQuestionReplyResponse.ImageResponse(
                        image.personalQuestionReplyImageId(),
                        image.imageUrl(),
                        image.width(),
                        image.height(),
                        image.sortOrder()))
            .toList());
  }

  public PersonalArtworkQuestionListResponse toResponse(PersonalArtworkQuestionListResult result) {
    return new PersonalArtworkQuestionListResponse(
        result.questions().stream().map(this::toQuestionItemResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  private PersonalArtworkQuestionItemResponse toQuestionItemResponse(
      PersonalArtworkQuestionItemResult result) {
    PersonalArtworkQuestionUserResponse user =
        result.user() == null
            ? null
            : new PersonalArtworkQuestionUserResponse(
                result.user().userId(), result.user().nickname(), result.user().isCreator());
    PersonalArtworkQuestionReplyItemResponse reply =
        result.reply() == null ? null : toQuestionReplyItemResponse(result.reply());

    return new PersonalArtworkQuestionItemResponse(
        result.personalQuestionId(),
        result.content(),
        result.isPublic(),
        result.accessible(),
        result.isMine(),
        result.canReply(),
        result.likeCount(),
        result.isLiked(),
        result.answerStatus(),
        result.createdAt(),
        result.images().stream()
            .map(
                image ->
                    new QuestionImageResponse(
                        image.personalQuestionImageId(),
                        image.imageUrl(),
                        image.width(),
                        image.height(),
                        image.sortOrder()))
            .toList(),
        user,
        reply);
  }

  private PersonalArtworkQuestionReplyItemResponse toQuestionReplyItemResponse(
      PersonalArtworkQuestionReplyItemResult result) {
    return new PersonalArtworkQuestionReplyItemResponse(
        result.personalQuestionReplyId(),
        result.userId(),
        result.nickname(),
        result.isCreator(),
        result.content(),
        result.createdAt(),
        result.images().stream()
            .map(
                image ->
                    new ReplyImageResponse(
                        image.personalQuestionReplyImageId(),
                        image.imageUrl(),
                        image.width(),
                        image.height(),
                        image.sortOrder()))
            .toList(),
        result.likeCount(),
        result.isLiked(),
        result.isMine());
  }
}
