package com.example.demo.domain.display.domain.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class DisplayDetailImagesTest {
  @Test
  void replacingDetailsPreservesImageIdsForOccupiedSlots() {
    Display display = display();
    display.addImage(
        new DisplayImage(21L, "https://example.com/old.png", DisplayImageType.DETAIL, 0, null));
    display.addImage(
        new DisplayImage(22L, "https://example.com/remove.png", DisplayImageType.DETAIL, 1, null));
    display.changeDetailImages(List.of("https://example.com/new.png"));
    var detail =
        display.getImages().stream()
            .filter(image -> image.getImageType() == DisplayImageType.DETAIL)
            .toList();
    assertThat(detail).hasSize(1);
    assertThat(detail.getFirst().getId()).isEqualTo(21L);
    assertThat(detail.getFirst().getImageUrl()).isEqualTo("https://example.com/new.png");
    assertThat(display.getPosterImageUrl()).isEqualTo("https://cdn.displayu.com/posters/main.png");
  }

  @Test
  void invalidReplacementLeavesExistingImagesUntouched() {
    Display display = display();
    display.changeDetailImages(List.of("https://example.com/old.png"));
    assertThatThrownBy(
            () -> display.changeDetailImages(List.of("https://example.com/new.png", " ")))
        .isInstanceOf(IllegalArgumentException.class);
    assertThat(display.getImages())
        .extracting(DisplayImage::getImageUrl)
        .containsExactly(
            "https://cdn.displayu.com/posters/main.png", "https://example.com/old.png");
  }

  private static Display display() {
    Display display =
        Display.create(
            new UserId(1L),
            "FORM 2026",
            "https://cdn.displayu.com/posters/main.png",
            "기존 부제",
            "기존 설명",
            new DisplayLocation(
                "기존 전시장", BigDecimal.valueOf(37.5513), BigDecimal.valueOf(126.9248)),
            "",
            "기존 유의사항",
            "중앙대학교",
            "디자인학부",
            DisplayType.GRADUATION,
            List.of(DisplayField.GRAPHIC),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 5, 28),
                LocalDate.of(2026, 6, 5),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.ON_EXHIBITION);
    return display;
  }
}
