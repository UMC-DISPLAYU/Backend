package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkFeelingCommand;
import com.example.demo.domain.artworkcommunication.application.command.ArtworkFeelingReplyCommand;
import com.example.demo.domain.artworkcommunication.application.command.UpdateArtworkFeelingCommand;
import com.example.demo.domain.artworkcommunication.application.query.GetArtworkFeelingsQuery;
import com.example.demo.domain.artworkcommunication.application.result.*;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.*;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingListResponse.ArtworkFeelingItemResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingListResponse.ArtworkFeelingReplyItemResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingListResponse.ArtworkFeelingUserResponse;
import org.springframework.stereotype.Component;

@Component
public class ArtworkFeelingPresentationMapper {

  public ArtworkFeelingCommand toCommand(
      Long artworkId, Long userId, CreateArtworkFeelingRequest request) {
    return new ArtworkFeelingCommand(artworkId, userId, request.content());
  }

  public GetArtworkFeelingsQuery toQuery(Long artworkId, Long cursorId) {
    return new GetArtworkFeelingsQuery(artworkId, cursorId);
  }

  public UpdateArtworkFeelingCommand toCommand(
      Long artworkId, Long feelingId, Long userId, UpdateArtworkFeelingRequest request) {
    return new UpdateArtworkFeelingCommand(artworkId, feelingId, userId, request.content());
  }

  public ArtworkFeelingReplyCommand toCommand(
      Long artworkId, Long feelingId, Long userId, CreateArtworkFeelingReplyRequest request) {
    return new ArtworkFeelingReplyCommand(artworkId, feelingId, userId, request.content());
  }

  public ArtworkFeelingResponse toResponse(ArtworkFeelingResult result) {
    return new ArtworkFeelingResponse(
        result.feelingId(), result.userId(), result.content(), result.createdAt());
  }

  public UpdatedArtworkFeelingResponse toResponse(UpdatedArtworkFeelingResult result) {
    return new UpdatedArtworkFeelingResponse(
        result.feelingId(), result.content(), result.updatedAt());
  }

  public DeletedArtworkFeelingResponse toResponse(DeletedArtworkFeelingResult result) {
    return new DeletedArtworkFeelingResponse(result.feelingId(), result.deletedAt());
  }

  public ArtworkFeelingReplyResponse toResponse(ArtworkFeelingReplyResult result) {
    return new ArtworkFeelingReplyResponse(
        result.feelingReplyId(),
        result.createdAt(),
        result.content(),
        result.feelingId(),
        result.userId(),
        result.nickname());
  }

  public ArtworkFeelingListResponse toResponse(ArtworkFeelingListResult result) {
    return new ArtworkFeelingListResponse(
        result.feelings().stream().map(this::toFeelingItemResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  private ArtworkFeelingItemResponse toFeelingItemResponse(
      ArtworkFeelingListResult.ArtworkFeelingItemResult result) {
    return new ArtworkFeelingItemResponse(
        result.feelingId(),
        result.content(),
        result.createdAt(),
        new ArtworkFeelingUserResponse(
            result.user().userId(), result.user().nickname(), result.user().isCreator()),
        result.replies().stream().map(this::toReplyItemResponse).toList());
  }

  private ArtworkFeelingReplyItemResponse toReplyItemResponse(
      ArtworkFeelingListResult.ArtworkFeelingReplyItemResult result) {
    return new ArtworkFeelingReplyItemResponse(
        result.userId(),
        result.nickname(),
        result.content(),
        result.createdAt(),
        result.isCreator());
  }

  public ArtworkFeelingLikeResponse toResponse(ArtworkFeelingLikeResult result) {
    return new ArtworkFeelingLikeResponse(
        result.feelingId(),
        result.liked(),
        result.likeCount(),
        result.createdAt(),
        result.deletedAt());
  }
}
