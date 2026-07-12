package com.example.demo.domain.artworkcommunication.presentation.mapper;

import com.example.demo.domain.artworkcommunication.application.command.ArtworkFeelingCommand;
import com.example.demo.domain.artworkcommunication.application.command.UpdateArtworkFeelingCommand;
import com.example.demo.domain.artworkcommunication.application.result.ArtworkFeelingResult;
import com.example.demo.domain.artworkcommunication.application.result.UpdatedArtworkFeelingResult;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.UpdatedArtworkFeelingResponse;
import org.springframework.stereotype.Component;

@Component
public class ArtworkFeelingPresentationMapper {

  public ArtworkFeelingCommand toCommand(
      Long artworkId, Long userId, CreateArtworkFeelingRequest request) {
    return new ArtworkFeelingCommand(artworkId, userId, request.content());
  }

  public UpdateArtworkFeelingCommand toCommand(
      Long artworkId, Long feelingId, Long userId, UpdateArtworkFeelingRequest request) {
    return new UpdateArtworkFeelingCommand(artworkId, feelingId, userId, request.content());
  }

  public ArtworkFeelingResponse toResponse(ArtworkFeelingResult result) {
    return new ArtworkFeelingResponse(
        result.feelingId(), result.userId(), result.content(), result.createdAt());
  }

  public UpdatedArtworkFeelingResponse toResponse(UpdatedArtworkFeelingResult result) {
    return new UpdatedArtworkFeelingResponse(
        result.feelingId(), result.content(), result.updatedAt());
  }
}
