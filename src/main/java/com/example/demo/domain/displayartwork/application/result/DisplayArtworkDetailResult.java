package com.example.demo.domain.displayartwork.application.result;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record DisplayArtworkDetailResult(
    Long artworkId,
    String artworkName,
    String content,
    String type,
    int productionYear,
    String size,
    String materialMedia,
    String point,
    List<ImageResult> images,
    String artistName,
    Long artistUserId,
    List<QaHandlerResult> qaHandlers,
    ExhibitionInfoResult exhibitionInfo,
    long likeCount,
    boolean isLiked,
    boolean isSaved) {

  public record QaHandlerResult(Long userId, String name) {}

  public static DisplayArtworkDetailResult of(
      DisplayArtwork displayArtwork,
      String artistName,
      Long artistUserId,
      List<QaHandlerResult> qaHandlers,
      long likeCount,
      boolean isLiked,
      boolean isSaved) {
    return new DisplayArtworkDetailResult(
        displayArtwork.getId(),
        displayArtwork.getArtworkName(),
        displayArtwork.getContent(),
        displayArtwork.getType().name(),
        displayArtwork.getProductionYear(),
        displayArtwork.getSize(),
        displayArtwork.getMaterialMedia(),
        displayArtwork.getPoint(),
        displayArtwork.getImages().stream().map(ImageResult::from).toList(),
        artistName,
        artistUserId,
        qaHandlers,
        ExhibitionInfoResult.from(displayArtwork),
        likeCount,
        isLiked,
        isSaved);
  }

  public record ImageResult(
      Long imageId,
      String imageUrl,
      boolean isThumbnail,
      String imageType,
      int sortOrder,
      String caption,
      int width,
      int height) {

    private static ImageResult from(ArtworkImage image) {
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

  public record ExhibitionInfoResult(
      Long displayId,
      String exhibitionTitle,
      String exhibitionThumbnailUrl,
      String exhibitionOrganizer,
      String exhibitionPeriod,
      String exhibitionLocation) {

    private static final DateTimeFormatter FULL_DATE = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("MM.dd");

    private static ExhibitionInfoResult from(DisplayArtwork displayArtwork) {
      var display = displayArtwork.getDisplay();
      var period = display.getPeriod();
      String formattedPeriod =
          "%s - %s"
              .formatted(period.startDate().format(FULL_DATE), period.endDate().format(SHORT_DATE));
      return new ExhibitionInfoResult(
          display.getId(),
          display.getTitle(),
          display.getPosterImageUrl(),
          organizerOf(display),
          formattedPeriod,
          display.getLocation().placeName());
    }

    // 프론트에서 그대로 노출하는 값이므로 "주최기관 부제" 형태로 합쳐서 전달한다.
    private static String organizerOf(Display display) {
      return "%s %s".formatted(display.getOrganization(), display.getSubtitle());
    }
  }
}
