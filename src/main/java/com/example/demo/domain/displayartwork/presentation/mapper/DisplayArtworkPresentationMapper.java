package com.example.demo.domain.displayartwork.presentation.mapper;

import com.example.demo.domain.displayartwork.application.result.DeleteDisplayArtworkResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkDetailResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkLikeResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkPreviewResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkResult;
import com.example.demo.domain.displayartwork.application.result.ReorderDisplayArtworksResult;
import com.example.demo.domain.displayartwork.presentation.response.DeleteDisplayArtworkResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkDetailResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkLikeResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkPreviewResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkResponse;
import com.example.demo.domain.displayartwork.presentation.response.ReorderDisplayArtworksResponse;
import org.springframework.stereotype.Component;

@Component
public class DisplayArtworkPresentationMapper {

  public DisplayArtworkLikeResponse toResponse(DisplayArtworkLikeResult result) {
    return new DisplayArtworkLikeResponse(result.artworkId(), result.isLiked(), result.likeCount());
  }

  public ReorderDisplayArtworksResponse toResponse(ReorderDisplayArtworksResult result) {
    return new ReorderDisplayArtworksResponse(result.displayId(), result.updatedCount());
  }

  public DeleteDisplayArtworkResponse toResponse(DeleteDisplayArtworkResult result) {
    return new DeleteDisplayArtworkResponse(result.deletedArtworkId(), result.message());
  }

  public DisplayArtworkPreviewResponse toResponse(DisplayArtworkPreviewResult result) {
    return new DisplayArtworkPreviewResponse(
        result.artworks().stream().map(this::toResponse).toList(),
        result.page(),
        result.size(),
        result.isLast());
  }

  private DisplayArtworkPreviewResponse.ArtworkCardResponse toResponse(
      DisplayArtworkPreviewResult.ArtworkCardResult result) {
    return new DisplayArtworkPreviewResponse.ArtworkCardResponse(
        result.artworkId(),
        result.artworkName(),
        result.artworkImageUrl(),
        result.imageWidth(),
        result.imageHeight(),
        toResponse(result.exhibitionInfo()));
  }

  private DisplayArtworkPreviewResponse.ExhibitionInfoResponse toResponse(
      DisplayArtworkPreviewResult.ExhibitionInfoResult result) {
    return new DisplayArtworkPreviewResponse.ExhibitionInfoResponse(
        result.displayId(),
        result.exhibitionTitle(),
        result.exhibitionPeriod(),
        result.exhibitionLocation());
  }

  public DisplayArtworkDetailResponse toResponse(DisplayArtworkDetailResult result) {
    return new DisplayArtworkDetailResponse(
        result.artworkId(),
        result.artworkName(),
        result.content(),
        result.type(),
        result.productionYear(),
        result.size(),
        result.materialMedia(),
        result.point(),
        result.images().stream().map(this::toResponse).toList(),
        result.artistName(),
        result.artistUserId(),
        toResponse(result.exhibitionInfo()),
        result.likeCount(),
        result.isLiked(),
        result.isSaved());
  }

  private DisplayArtworkDetailResponse.ImageResponse toResponse(
      DisplayArtworkDetailResult.ImageResult result) {
    return new DisplayArtworkDetailResponse.ImageResponse(
        result.imageId(),
        result.imageUrl(),
        result.isThumbnail(),
        result.imageType(),
        result.sortOrder(),
        result.caption(),
        result.width(),
        result.height());
  }

  private DisplayArtworkDetailResponse.ExhibitionInfoResponse toResponse(
      DisplayArtworkDetailResult.ExhibitionInfoResult result) {
    return new DisplayArtworkDetailResponse.ExhibitionInfoResponse(
        result.displayId(),
        result.exhibitionTitle(),
        result.exhibitionPeriod(),
        result.exhibitionLocation());
  }

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
        result.images().stream().map(this::toResponse).toList(),
        result.artistName(),
        result.artistUserId(),
        result.coAuthorCount(),
        result.qaHandlerUserId());
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
