package com.example.demo.domain.displayartwork.application.result;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import java.util.List;

/** 수정 화면 진입 시 등록 당시 상태를 그대로 복원하기 위한 전체 필드. */
public record DisplayArtworkEditResult(
    Long artworkId,
    Long displayId,
    String artworkName,
    String content,
    String type,
    int productionYear,
    String materialMedia,
    String size,
    String point,
    List<ImageResult> images,
    String artistName,
    Long artistUserId,
    List<CoAuthorResult> coAuthors,
    List<Long> qaHandlerUserIds) {

  public record CoAuthorResult(Long userId, String name) {}

  public record ImageResult(
      Long imageId,
      String imageUrl,
      boolean isThumbnail,
      String imageType,
      int sortOrder,
      String caption,
      int width,
      int height) {

    public static ImageResult from(ArtworkImage image) {
      return new ImageResult(
          image.getId(),
          image.getImageUrl(),
          image.isThumbnail(),
          image.getImageType().name(),
          image.getSortOrder(),
          image.getCaption(),
          image.getWidth(),
          image.getHeight());
    }
  }

  public static DisplayArtworkEditResult of(
      DisplayArtwork artwork,
      String artistName,
      Long artistUserId,
      List<CoAuthorResult> coAuthors,
      List<Long> qaHandlerUserIds) {
    return new DisplayArtworkEditResult(
        artwork.getId(),
        artwork.getDisplay().getId(),
        artwork.getArtworkName(),
        artwork.getContent(),
        artwork.getType().name(),
        artwork.getProductionYear(),
        artwork.getMaterialMedia(),
        artwork.getSize(),
        artwork.getPoint(),
        artwork.getImages().stream().map(ImageResult::from).toList(),
        artistName,
        artistUserId,
        coAuthors,
        qaHandlerUserIds);
  }
}
