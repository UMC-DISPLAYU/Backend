package com.example.demo.domain.displayartwork.presentation.mapper;

import com.example.demo.domain.displayartwork.application.command.ArtworkImageCommand;
import com.example.demo.domain.displayartwork.application.command.CreateDisplayArtworkCommand;
import com.example.demo.domain.displayartwork.application.command.ReorderDisplayArtworksCommand;
import com.example.demo.domain.displayartwork.application.command.UpdateDisplayArtworkCommand;
import com.example.demo.domain.displayartwork.application.result.DeleteDisplayArtworkResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkByArtistResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkDetailResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkEditResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkLikeResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkListResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkPreviewResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkResult;
import com.example.demo.domain.displayartwork.application.result.ReorderDisplayArtworksResult;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.presentation.request.CreateDisplayArtworkRequest;
import com.example.demo.domain.displayartwork.presentation.request.ReorderDisplayArtworksRequest;
import com.example.demo.domain.displayartwork.presentation.request.UpdateDisplayArtworkRequest;
import com.example.demo.domain.displayartwork.presentation.response.DeleteDisplayArtworkResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkByArtistResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkDetailResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkDetailResponse.CoAuthorResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkDetailResponse.QaHandlerResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkEditResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkLikeResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkListResponse;
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
        result.artistName(),
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

  public DisplayArtworkByArtistResponse toResponse(DisplayArtworkByArtistResult result) {
    return new DisplayArtworkByArtistResponse(
        result.artworks().stream().map(this::toResponse).toList());
  }

  private DisplayArtworkByArtistResponse.ArtworkCardResponse toResponse(
      DisplayArtworkByArtistResult.ArtworkCardResult result) {
    return new DisplayArtworkByArtistResponse.ArtworkCardResponse(
        result.artworkId(),
        result.artworkName(),
        result.artistName(),
        result.artworkImageUrl(),
        result.imageWidth(),
        result.imageHeight(),
        result.createdAt(),
        toResponse(result.exhibitionInfo()));
  }

  private DisplayArtworkByArtistResponse.ExhibitionInfoResponse toResponse(
      DisplayArtworkByArtistResult.ExhibitionInfoResult result) {
    return new DisplayArtworkByArtistResponse.ExhibitionInfoResponse(
        result.displayId(),
        result.exhibitionTitle(),
        result.exhibitionPeriod(),
        result.exhibitionLocation());
  }

  public DisplayArtworkListResponse toResponse(DisplayArtworkListResult result) {
    return new DisplayArtworkListResponse(
        result.artworks().stream().map(this::toResponse).toList());
  }

  private DisplayArtworkListResponse.ArtworkItemResponse toResponse(
      DisplayArtworkListResult.ArtworkItemResult result) {
    return new DisplayArtworkListResponse.ArtworkItemResponse(
        result.artworkId(),
        result.artworkName(),
        result.artistName(),
        result.artworkImageUrl(),
        result.imageWidth(),
        result.imageHeight());
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
        result.coAuthors().stream()
            .map(coAuthor -> new CoAuthorResponse(coAuthor.userId(), coAuthor.name()))
            .toList(),
        result.qaHandlers().stream()
            .map(handler -> new QaHandlerResponse(handler.userId(), handler.name()))
            .toList(),
        toResponse(result.exhibitionInfo()),
        result.likeCount(),
        result.isLiked(),
        result.isArchived());
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
        result.exhibitionThumbnailUrl(),
        result.exhibitionOrganizer(),
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
        result.qaHandlerUserIds());
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

  public DisplayArtworkEditResponse toResponse(DisplayArtworkEditResult result) {
    return new DisplayArtworkEditResponse(
        result.artworkId(),
        result.displayId(),
        result.artworkName(),
        result.content(),
        result.type(),
        result.productionYear(),
        result.materialMedia(),
        result.size(),
        result.point(),
        result.images().stream()
            .map(
                image ->
                    new DisplayArtworkEditResponse.ImageResponse(
                        image.imageId(),
                        image.imageUrl(),
                        image.isThumbnail(),
                        image.imageType(),
                        image.sortOrder(),
                        image.caption(),
                        image.width(),
                        image.height()))
            .toList(),
        result.artistName(),
        result.artistUserId(),
        result.coAuthors().stream()
            .map(
                coAuthor ->
                    new DisplayArtworkEditResponse.CoAuthorResponse(
                        coAuthor.userId(), coAuthor.name()))
            .toList(),
        result.qaHandlerUserIds());
  }

  public CreateDisplayArtworkCommand toCommand(CreateDisplayArtworkRequest request) {
    return new CreateDisplayArtworkCommand(
        request.displayId(),
        request.artworkName(),
        request.content(),
        toArtworkType(request.type()),
        request.productionYear(),
        request.materialMedia(),
        request.size(),
        request.point(),
        request.images().stream().map(this::toCommand).toList(),
        request.artistName(),
        request.artistUserId(),
        request.coAuthors().userIds(),
        request.coAuthors().rawNames(),
        request.qaHandlerUserIds());
  }

  private ArtworkImageCommand toCommand(CreateDisplayArtworkRequest.ImageRequest image) {
    return new ArtworkImageCommand(
        image.imageUrl(),
        image.isThumbnail(),
        image.imageType(),
        image.sortOrder(),
        image.caption(),
        image.width(),
        image.height());
  }

  // API에 노출하는 분야 값(Field)과 도메인 타입(ArtworkType)이 별개라 여기서 명시적으로 변환한다.
  private ArtworkType toArtworkType(CreateDisplayArtworkRequest.Field field) {
    return switch (field) {
      case PAINTING -> ArtworkType.PAINTING;
      case DESIGN -> ArtworkType.DESIGN;
      case PHOTOGRAPHY -> ArtworkType.PHOTOGRAPHY;
      case ARCHITECTURE -> ArtworkType.ARCHITECTURE;
      case MEDIA -> ArtworkType.MEDIA;
      case CRAFT -> ArtworkType.CRAFT;
      case SCULPTURE -> ArtworkType.SCULPTURE;
      case FASHION -> ArtworkType.FASHION;
      case COMPLEX -> ArtworkType.COMPLEX;
      case ETC -> ArtworkType.ETC;
    };
  }

  public ReorderDisplayArtworksCommand toCommand(ReorderDisplayArtworksRequest request) {
    return new ReorderDisplayArtworksCommand(request.displayId(), request.orderedArtworkIds());
  }

  public UpdateDisplayArtworkCommand toCommand(
      Long artworkId, UpdateDisplayArtworkRequest request) {
    return new UpdateDisplayArtworkCommand(
        artworkId,
        request.artworkName(),
        request.content(),
        request.type(),
        request.productionYear(),
        request.materialMedia(),
        request.size(),
        request.point(),
        request.images().stream().map(this::toCommand).toList(),
        request.artistName(),
        request.artistUserId(),
        request.coAuthors().userIds(),
        request.coAuthors().rawNames(),
        request.qaHandlerUserIds());
  }

  private ArtworkImageCommand toCommand(UpdateDisplayArtworkRequest.ImageRequest image) {
    return new ArtworkImageCommand(
        image.imageUrl(),
        image.isThumbnail(),
        image.imageType(),
        image.sortOrder(),
        image.caption(),
        image.width(),
        image.height());
  }
}
