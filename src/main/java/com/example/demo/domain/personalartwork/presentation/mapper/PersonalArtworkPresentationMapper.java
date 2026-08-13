package com.example.demo.domain.personalartwork.presentation.mapper;

import com.example.demo.domain.personalartwork.application.command.PersonalArtworkContentCommand;
import com.example.demo.domain.personalartwork.application.command.PersonalArtworkImageCommand;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkLikeResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.presentation.request.PersonalArtworkRequest;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkLikeResponse;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkResponse;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class PersonalArtworkPresentationMapper {

  public PersonalArtworkLikeResponse toResponse(PersonalArtworkLikeResult result) {
    return new PersonalArtworkLikeResponse(
        result.personalArtworkId(), result.isLiked(), result.likeCount());
  }

  public PersonalArtworkSummaryResponse toResponse(PersonalArtworkSummaryResult result) {
    return new PersonalArtworkSummaryResponse(
        result.personalArtworkId(),
        result.artworkName(),
        result.thumbnailUrl(),
        result.type(),
        result.types(),
        result.createdAt());
  }

  public PersonalArtworkResponse toResponse(PersonalArtworkResult result) {
    return new PersonalArtworkResponse(
        result.personalArtworkId(),
        result.userId(),
        result.artistName(),
        result.artworkName(),
        result.content(),
        result.type(),
        result.types(),
        result.productionYear(),
        result.materialMedia(),
        result.size(),
        result.point(),
        result.createdAt(),
        result.images().stream().map(this::toResponse).toList(),
        result.likeCount(),
        result.isLiked(),
        result.isArchived());
  }

  public PersonalArtworkContentCommand toCommand(PersonalArtworkRequest request) {
    return new PersonalArtworkContentCommand(
        request.artworkName(),
        request.content(),
        request.resolvedTypes(),
        request.productionYear(),
        request.materialMedia(),
        request.size(),
        request.point(),
        request.images().stream().map(this::toCommand).toList());
  }

  private PersonalArtworkImageCommand toCommand(PersonalArtworkRequest.ImageRequest image) {
    return new PersonalArtworkImageCommand(
        image.imageUrl(),
        image.isThumbnail(),
        image.imageType(),
        image.sortOrder(),
        image.caption(),
        image.width(),
        image.height());
  }

  private PersonalArtworkResponse.ImageResponse toResponse(
      PersonalArtworkResult.ImageResult result) {
    return new PersonalArtworkResponse.ImageResponse(
        result.imageId(),
        result.imageUrl(),
        result.isThumbnail(),
        result.imageType(),
        result.sortOrder(),
        result.caption(),
        result.width(),
        result.height());
  }
}
