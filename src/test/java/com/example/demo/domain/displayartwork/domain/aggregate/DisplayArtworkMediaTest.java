package com.example.demo.domain.displayartwork.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.file.domain.MediaContent;
import com.example.demo.global.file.domain.MediaType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class DisplayArtworkMediaTest {
  @Test
  void legacyImageKeepsItsCardUrl() {
    ArtworkImage image = artwork(List.of(ArtworkType.VIDEO)).getImages().getFirst();
    assertThat(image.getMediaType()).isEqualTo(MediaType.IMAGE);
    assertThat(image.cardImageUrl()).isEqualTo(image.getImageUrl());
    assertThat(image.videoUrl()).isNull();
  }

  @Test
  void videoKeepsOriginalAndThumbnailSeparate() {
    DisplayArtwork artwork = artwork(List.of(ArtworkType.VIDEO));
    artwork.replaceImages(List.of(video(ArtworkImageType.ARTWORK)));
    ArtworkImage video = artwork.getImages().getFirst();
    assertThat(video.getImageUrl()).isEqualTo("https://cdn.example/artwork/movie.mp4");
    assertThat(video.cardImageUrl()).isEqualTo("https://cdn.example/artwork/thumbnail.png");
    assertThat(video.videoUrl()).isEqualTo(video.getImageUrl());
  }

  @Test
  void invalidReplacementKeepsExistingChildrenAndAssociations() {
    DisplayArtwork artwork = artwork(List.of(ArtworkType.VIDEO));
    ArtworkImage original = artwork.getImages().getFirst();
    assertThatThrownBy(
            () ->
                artwork.replaceImages(
                    List.of(video(ArtworkImageType.ARTWORK), video(ArtworkImageType.ARTWORK))))
        .isInstanceOf(BusinessException.class);
    assertThat(artwork.getImages()).containsExactly(original);
    assertThat(original.getDisplayArtwork()).isSameAs(artwork);
  }

  @Test
  void replacingFromOwnImageViewDoesNotClearTheInput() {
    DisplayArtwork artwork = artwork(List.of(ArtworkType.VIDEO));
    ArtworkImage original = artwork.getImages().getFirst();
    artwork.replaceImages(artwork.getImages());
    assertThat(artwork.getImages()).containsExactly(original);
  }

  @Test
  void legacyReplacementCannotReclassifyExistingVideoAsImage() {
    DisplayArtwork artwork = artwork(List.of(ArtworkType.VIDEO));
    ArtworkImage video = video(ArtworkImageType.ARTWORK);
    artwork.replaceImages(List.of(video));
    ArtworkImage legacyImage =
        new ArtworkImage(
            null, video.getImageUrl(), true, ArtworkImageType.ARTWORK, 0, null, 800, 600);
    assertThatThrownBy(() -> artwork.replaceImages(List.of(legacyImage)))
        .isInstanceOf(BusinessException.class);
    assertThat(artwork.getImages()).containsExactly(video);
    assertThat(video.cardImageUrl()).isEqualTo("https://cdn.example/artwork/thumbnail.png");
  }

  @Test
  void directAdditionCannotBypassVideoLimit() {
    DisplayArtwork artwork = artwork(List.of(ArtworkType.VIDEO));
    artwork.addImage(video(ArtworkImageType.ARTWORK));
    assertThatThrownBy(() -> artwork.addImage(video(ArtworkImageType.ARTWORK)))
        .isInstanceOf(BusinessException.class);
    assertThat(artwork.getImages()).hasSize(2);
  }

  @Test
  void processVideoIsRejectedEvenWhenAnArtworkImageExists() {
    DisplayArtwork artwork = artwork(List.of(ArtworkType.VIDEO));
    assertThatThrownBy(() -> artwork.addImage(video(ArtworkImageType.WORK_PROCESS)))
        .isInstanceOf(BusinessException.class);
    assertThat(artwork.getImages()).hasSize(1);
  }

  @Test
  void allowsTenArtworkItemsButRejectsElevenWithoutChangingTheTen() {
    DisplayArtwork artwork = artwork(List.of(ArtworkType.VIDEO));
    List<ArtworkImage> images =
        IntStream.range(0, 10)
            .mapToObj(
                i ->
                    new ArtworkImage(
                        null,
                        "https://cdn.example/" + i + ".png",
                        i == 0,
                        ArtworkImageType.ARTWORK,
                        i,
                        null,
                        800,
                        600))
            .toList();
    artwork.replaceImages(images);
    assertThatThrownBy(() -> artwork.addImage(video(ArtworkImageType.ARTWORK)))
        .isInstanceOf(BusinessException.class);
    assertThat(artwork.getImages()).containsExactlyElementsOf(images);
  }

  private static ArtworkImage video(ArtworkImageType imageType) {
    return new ArtworkImage(
        null,
        MediaContent.video(
            "https://cdn.example/artwork/movie.mp4", "https://cdn.example/artwork/thumbnail.png"),
        true,
        imageType,
        0,
        null,
        800,
        600);
  }

  private static DisplayArtwork artwork(List<ArtworkType> types) {
    return DisplayArtwork.create(
        display(),
        "작품명",
        "작품 설명",
        types,
        2026,
        "재료",
        "100x100cm",
        "포인트",
        0,
        1L,
        List.of(
            new ArtworkImage(
                null,
                "https://cdn.displayu.com/artworks/main.png",
                true,
                ArtworkImageType.ARTWORK,
                0,
                "대표 이미지",
                1200,
                1600)));
  }

  private static Display display() {
    return Display.create(
        new UserId(1L),
        "FORM 2026",
        "https://cdn.displayu.com/posters/main.png",
        "subtitle",
        "content",
        new DisplayLocation("전시장", new BigDecimal("37.5513"), new BigDecimal("126.9248")),
        "",
        "",
        "organization",
        "department",
        DisplayType.GRADUATION,
        List.of(DisplayField.DESIGN),
        DisplayRegion.SEOUL,
        new DisplayPeriod(
            LocalDate.of(2026, 5, 28),
            LocalDate.of(2026, 6, 5),
            LocalTime.of(10, 0),
            LocalTime.of(18, 0)),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.IMMEDIATELY);
  }
}
