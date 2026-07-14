package com.example.demo.domain.displayartwork.presentation.mapper;

import com.example.demo.domain.displayartwork.application.result.DisplayArtworkResult;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkResponse;
import org.springframework.stereotype.Component;

@Component
public class DisplayArtworkPresentationMapper {

  public DisplayArtworkResponse toResponse(DisplayArtworkResult result) {
    return new DisplayArtworkResponse(
        result.artworkId(),
        result.displayId(),
        result.artworkName(),
        result.content(),
        result.type(),
        result.productionYear(),
        result.materialMedia(),
        result.size(),
        result.point(),
        result.workSortOrder(),
        result.images().stream().map(this::toResponse).toList());
  }

  private DisplayArtworkResponse.ImageResponse toResponse(DisplayArtworkResult.ImageResult result) {
    return new DisplayArtworkResponse.ImageResponse(
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
