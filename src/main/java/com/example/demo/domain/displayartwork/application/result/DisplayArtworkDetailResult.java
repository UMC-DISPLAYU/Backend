package com.example.demo.domain.displayartwork.application.result;

import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record DisplayArtworkDetailResult(
    Long artworkId,
    String artworkName,
    String content,
    String type,
    List<String> types,
    int productionYear,
    String size,
    String materialMedia,
    String point,
    List<ImageResult> images,
    String artistName,
    Long artistUserId,
    List<CoAuthorResult> coAuthors,
    List<QaHandlerResult> qaHandlers,
    ExhibitionInfoResult exhibitionInfo,
    long likeCount,
    boolean isLiked,
    boolean isArchived) {

  public record QaHandlerResult(Long userId, String name) {}

  /** 계정 없이 이름만 입력한 공동 작업자는 userId가 null이다. */
  public record CoAuthorResult(Long userId, String name) {}

  public static DisplayArtworkDetailResult of(
      DisplayArtwork displayArtwork,
      String artistName,
      Long artistUserId,
      List<CoAuthorResult> coAuthors,
      List<QaHandlerResult> qaHandlers,
      long likeCount,
      boolean isLiked,
      boolean isArchived) {
    return new DisplayArtworkDetailResult(
        displayArtwork.getId(),
        displayArtwork.getArtworkName(),
        displayArtwork.getContent(),
        displayArtwork.getType().name(),
        displayArtwork.getFieldTypes().stream().map(Enum::name).toList(),
        displayArtwork.getProductionYear(),
        displayArtwork.getSize(),
        displayArtwork.getMaterialMedia(),
        displayArtwork.getPoint(),
        displayArtwork.getImages().stream().map(ImageResult::from).toList(),
        artistName,
        artistUserId,
        coAuthors,
        qaHandlers,
        ExhibitionInfoResult.from(displayArtwork),
        likeCount,
        isLiked,
        isArchived);
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
      String exhibitionSubtitle,
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
          display.getSubtitle(),
          display.getPosterImageUrl(),
          display.getOrganization(),
          formattedPeriod,
          display.getLocation().placeName());
    }
  }
}
