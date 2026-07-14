package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkQuestionReplyCommand;
import com.example.demo.domain.artworkcommunication.application.command.CreateArtworkQuestionCommand;
import com.example.demo.domain.artworkcommunication.application.command.UpdateArtworkQuestionCommand;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionReplyResult;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.application.result.DeletedArtworkQuestionResult;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkQuestionRequest;
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

  public ArtworkQuestionResponse toResponse(ArtworkQuestionResult result) {
    return new ArtworkQuestionResponse(
        result.artQueId(),
        result.content(),
        result.isPublic(),
        result.answerStatus(),
        result.createdAt(),
        result.updatedAt(),
        result.deletedAt(),
        result.displayArtworkId(),
        result.userId());
  }

  public DeletedArtworkQuestionResponse toResponse(DeletedArtworkQuestionResult result) {
    return new DeletedArtworkQuestionResponse(result.artQueId(), result.deletedAt());
  }

  public ArtworkQuestionReplyResponse toResponse(ArtworkQuestionReplyResult result) {
    return new ArtworkQuestionReplyResponse(
        result.queReplyId(),
        result.content(),
        result.createdAt(),
        result.updatedAt(),
        result.deletedAt(),
        result.artQueId(),
        result.creatorId(),
        result.creatorName());
  }
}
