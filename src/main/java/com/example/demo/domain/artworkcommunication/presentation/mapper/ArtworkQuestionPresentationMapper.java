package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkQuestionReplyCommand;
import com.example.demo.domain.artworkcommunication.application.command.CreateArtworkQuestionCommand;
import com.example.demo.domain.artworkcommunication.application.command.UpdateArtworkQuestionCommand;
import com.example.demo.domain.artworkcommunication.application.query.GetArtworkQuestionsQuery;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.application.result.DeletedArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionReplyResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.DeletedArtworkQuestionResponse;
import org.springframework.stereotype.Component;

@Component
public class ArtworkQuestionPresentationMapper {

  public CreateArtworkQuestionCommand toCommand(
      Long artworkId, Long userId, CreateArtworkQuestionRequest request) {
    return new CreateArtworkQuestionCommand(
        artworkId, userId, request.content(), request.isPublic());
  }

  public UpdateArtworkQuestionCommand toCommand(
      Long artworkId, Long questionId, Long userId, UpdateArtworkQuestionRequest request) {
    return new UpdateArtworkQuestionCommand(
        artworkId, questionId, userId, request.content(), request.isPublic());
  }

  public ArtworkQuestionReplyCommand toCommand(
      Long artworkId, Long questionId, Long userId, CreateArtworkQuestionReplyRequest request) {
    return new ArtworkQuestionReplyCommand(artworkId, questionId, userId, request.content());
  }

  public GetArtworkQuestionsQuery toQuery(Long artworkId, Long cursorId) {
    return new GetArtworkQuestionsQuery(artworkId, cursorId);
  }

  public ArtworkQuestionResponse toResponse(ArtworkQuestionResult result) {
    return new ArtworkQuestionResponse(
        result.questionId(),
        result.content(),
        result.isPublic(),
        result.answerStatus(),
        result.createdAt(),
        result.displayArtworkId(),
        result.userId());
  }

  public DeletedArtworkQuestionResponse toResponse(DeletedArtworkQuestionResult result) {
    return new DeletedArtworkQuestionResponse(result.questionId(), result.deletedAt());
  }

  public ArtworkQuestionReplyResponse toResponse(ArtworkQuestionReplyResult result) {
    return new ArtworkQuestionReplyResponse(
        result.queReplyId(),
        result.content(),
        result.createdAt(),
        result.questionId(),
        result.creatorId(),
        result.creatorName());
  }

  public ArtworkQuestionListResponse toResponse(ArtworkQuestionListResult result) {
    return new ArtworkQuestionListResponse(
        result.questions().stream().map(this::toQuestionItemResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  private ArtworkQuestionListResponse.ArtworkQuestionItemResponse toQuestionItemResponse(
      ArtworkQuestionListResult.ArtworkQuestionItemResult result) {
    return new ArtworkQuestionListResponse.ArtworkQuestionItemResponse(
        result.questionId(),
        result.content(),
        result.isPublic(),
        result.answerStatus(),
        result.createdAt(),
        new ArtworkQuestionListResponse.ArtworkQuestionUserResponse(
            result.user().userId(), result.user().nickname()),
        toQuestionReplyItemResponse(result.reply()));
  }

  private ArtworkQuestionListResponse.ArtworkQuestionReplyItemResponse toQuestionReplyItemResponse(
      ArtworkQuestionListResult.ArtworkQuestionReplyItemResult result) {
    if (result == null) {
      return null;
    }

    return new ArtworkQuestionListResponse.ArtworkQuestionReplyItemResponse(
        result.creatorId(),
        result.creatorName(),
        result.isCreator(),
        result.content(),
        result.createdAt());
  }
}
