package com.example.demo.domain.displayartwork.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.error.DisplayArtworkErrorCode;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.global.error.BusinessException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

/** 작품 분야는 1개 이상 2개 이하다. type 컬럼은 아직 여러 응답이 쓰므로 첫 번째 분야와 항상 같아야 한다. */
class DisplayArtworkFieldsTest {

  @Test
  void keepsSingleField() {
    DisplayArtwork artwork = artwork(List.of(ArtworkType.PAINTING));

    assertThat(artwork.getFieldTypes()).containsExactly(ArtworkType.PAINTING);
    assertThat(artwork.getType()).isEqualTo(ArtworkType.PAINTING);
  }

  @Test
  void keepsTwoFieldsInOrder() {
    DisplayArtwork artwork = artwork(List.of(ArtworkType.PHOTOGRAPHY, ArtworkType.MEDIA));

    assertThat(artwork.getFieldTypes()).containsExactly(ArtworkType.PHOTOGRAPHY, ArtworkType.MEDIA);
    // 하위 호환: type은 첫 번째 분야를 따라간다.
    assertThat(artwork.getType()).isEqualTo(ArtworkType.PHOTOGRAPHY);
  }

  @Test
  void treatsDuplicatedFieldAsSingleSelection() {
    DisplayArtwork artwork = artwork(List.of(ArtworkType.DESIGN, ArtworkType.DESIGN));

    assertThat(artwork.getFieldTypes()).containsExactly(ArtworkType.DESIGN);
  }

  @Test
  void rejectsMoreThanTwoFields() {
    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(
            () ->
                artwork(List.of(ArtworkType.PAINTING, ArtworkType.DESIGN, ArtworkType.PHOTOGRAPHY)))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayArtworkErrorCode.INVALID_ARTWORK_FIELD_COUNT));
  }

  @Test
  void rejectsEmptyFields() {
    assertThatExceptionOfType(BusinessException.class)
        .isThrownBy(() -> artwork(List.of()))
        .satisfies(
            exception ->
                assertThat(exception.errorCode())
                    .isEqualTo(DisplayArtworkErrorCode.INVALID_ARTWORK_FIELD_COUNT));
  }

  @Test
  void replacesFieldsOnContentChange() {
    DisplayArtwork artwork = artwork(List.of(ArtworkType.PAINTING));

    artwork.changeContent(
        "수정된 작품",
        "수정된 설명",
        List.of(ArtworkType.CRAFT, ArtworkType.SCULPTURE),
        2026,
        "혼합재료",
        "100x100cm",
        "포인트");

    assertThat(artwork.getFieldTypes()).containsExactly(ArtworkType.CRAFT, ArtworkType.SCULPTURE);
    assertThat(artwork.getType()).isEqualTo(ArtworkType.CRAFT);
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
