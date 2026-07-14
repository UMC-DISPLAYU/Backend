package com.example.demo.domain.personalartwork.presentation.mapper;

import com.example.demo.domain.personalartwork.application.result.PersonalArtworkResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkResponse;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkSummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class PersonalArtworkPresentationMapper {

  public PersonalArtworkSummaryResponse toResponse(PersonalArtworkSummaryResult result) {
    return new PersonalArtworkSummaryResponse(
        result.personalArtworkId(),
        result.artworkName(),
        result.thumbnailUrl(),
        result.type(),
        result.createdAt());
  }

  public PersonalArtworkResponse toResponse(PersonalArtworkResult result) {
    return new PersonalArtworkResponse(
        result.personalArtworkId(),
        result.userId(),
        result.artworkName(),
        result.content(),
        result.type(),
        result.productionYear(),
        result.materialMedia(),
        result.size(),
        result.point(),
        result.createdAt(),
        result.images().stream().map(this::toResponse).toList());
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
