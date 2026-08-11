package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkQuestionReplyCommand;
import com.example.demo.domain.artworkcommunication.application.command.CreateArtworkQuestionCommand;
import com.example.demo.domain.artworkcommunication.application.query.GetArtworkQuestionsQuery;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionLikeResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionListResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyLikeResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.application.result.DeletedArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.application.result.DeletedArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestion;
import com.example.demo.domain.artworkcommunication.domain.aggregate.ArtworkQuestionReply;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionLikeResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionReplyLikeResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionReplyResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.DeletedArtworkQuestionReplyResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.DeletedArtworkQuestionResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ArtworkQuestionPresentationMapper {

  public CreateArtworkQuestionCommand toCommand(
      Long artworkId, Long userId, CreateArtworkQuestionRequest request) {
    List<ArtworkQuestion.ImageInfo> images =
        request.images() == null
            ? List.of()
            : request.images().stream()
                .map(
                    image ->
                        new ArtworkQuestion.ImageInfo(
                            image.imageUrl(), image.width(), image.height()))
                .toList();
    return new CreateArtworkQuestionCommand(
        artworkId, userId, request.content(), request.isPublic(), images);
  }

  public ArtworkQuestionReplyCommand toCommand(
      Long artworkId, Long questionId, Long userId, CreateArtworkQuestionReplyRequest request) {
    List<ArtworkQuestionReply.ImageInfo> images =
        request.images() == null
            ? List.of()
            : request.images().stream()
                .map(
                    image ->
                        new ArtworkQuestionReply.ImageInfo(
                            image.imageUrl(), image.width(), image.height()))
                .toList();
    return new ArtworkQuestionReplyCommand(
        artworkId, questionId, userId, request.content(), images);
  }

  public GetArtworkQuestionsQuery toQuery(Long artworkId, Long cursorId, int size, Long userId) {
    return new GetArtworkQuestionsQuery(artworkId, cursorId, size, userId);
  }

  public ArtworkQuestionResponse toResponse(ArtworkQuestionResult result) {
    return new ArtworkQuestionResponse(
        result.questionId(),
        result.content(),
        result.isPublic(),
        result.answerStatus(),
        result.createdAt(),
        result.displayArtworkId(),
        result.userId(),
        result.images().stream()
            .map(
                image ->
                    new ArtworkQuestionResponse.ImageResponse(
                        image.questionImageId(),
                        image.imageUrl(),
                        image.width(),
                        image.height(),
                        image.sortOrder()))
            .toList());
  }

  public DeletedArtworkQuestionResponse toResponse(DeletedArtworkQuestionResult result) {
    return new DeletedArtworkQuestionResponse(result.questionId(), result.deletedAt());
  }

  public DeletedArtworkQuestionReplyResponse toResponse(DeletedArtworkQuestionReplyResult result) {
    return new DeletedArtworkQuestionReplyResponse(result.questionReplyId(), result.deletedAt());
  }

  public ArtworkQuestionReplyResponse toResponse(ArtworkQuestionReplyResult result) {
    return new ArtworkQuestionReplyResponse(
        result.queReplyId(),
        result.content(),
        result.createdAt(),
        result.questionId(),
        result.creatorId(),
        result.creatorName(),
        result.images().stream()
            .map(
                image ->
                    new ArtworkQuestionReplyResponse.ImageResponse(
                        image.questionReplyImageId(),
                        image.imageUrl(),
                        image.width(),
                        image.height(),
                        image.sortOrder()))
            .toList());
  }

  public ArtworkQuestionListResponse toResponse(ArtworkQuestionListResult result) {
    return new ArtworkQuestionListResponse(
        result.questions().stream().map(this::toQuestionItemResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  public ArtworkQuestionLikeResponse toResponse(ArtworkQuestionLikeResult result) {
    return new ArtworkQuestionLikeResponse(
        result.questionId(),
        result.liked(),
        result.likeCount(),
        result.createdAt(),
        result.deletedAt());
  }

  public ArtworkQuestionReplyLikeResponse toResponse(ArtworkQuestionReplyLikeResult result) {
    return new ArtworkQuestionReplyLikeResponse(
        result.questionReplyId(),
        result.liked(),
        result.likeCount(),
        result.createdAt(),
        result.deletedAt());
  }

  private ArtworkQuestionListResponse.ArtworkQuestionItemResponse toQuestionItemResponse(
      ArtworkQuestionListResult.ArtworkQuestionItemResult result) {
    return new ArtworkQuestionListResponse.ArtworkQuestionItemResponse(
        result.questionId(),
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
                    new ArtworkQuestionListResponse.QuestionImageResponse(
                        image.questionImageId(),
                        image.imageUrl(),
                        image.width(),
                        image.height(),
                        image.sortOrder()))
            .toList(),
        result.user() == null
            ? null
            : new ArtworkQuestionListResponse.ArtworkQuestionUserResponse(
                result.user().userId(), result.user().nickname(), result.user().isCreator()),
        toQuestionReplyItemResponse(result.reply()));
  }

  private ArtworkQuestionListResponse.ArtworkQuestionReplyItemResponse toQuestionReplyItemResponse(
      ArtworkQuestionListResult.ArtworkQuestionReplyItemResult result) {
    if (result == null) {
      return null;
    }

    return new ArtworkQuestionListResponse.ArtworkQuestionReplyItemResponse(
        result.questionReplyId(),
        result.creatorId(),
        result.creatorName(),
        result.isCreator(),
        result.content(),
        result.createdAt(),
        result.images().stream()
            .map(
                image ->
                    new ArtworkQuestionListResponse.ReplyImageResponse(
                        image.questionReplyImageId(),
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
