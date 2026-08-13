package com.example.demo.domain.displayartwork.application.result;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class DisplayArtworkDetailResultTest {

  @Test
  void separatesExhibitionSubtitleFromOrganizer() {
    Display display =
        Display.create(
            new UserId(1L),
            "FORM 2026",
            "https://cdn.displayu.com/posters/main.png",
            "디자인학부 졸업전시",
            "content",
            new DisplayLocation("전시장", new BigDecimal("37.5513"), new BigDecimal("126.9248")),
            "",
            "",
            "디유대학교",
            "디자인학부",
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
    DisplayArtwork artwork =
        DisplayArtwork.create(
            display,
            "작품명",
            "작품 설명",
            List.of(ArtworkType.DESIGN),
            2026,
            "캔버스",
            "100x100",
            "감상 포인트",
            0,
            1L,
            List.of(
                new ArtworkImage(
                    null,
                    "https://cdn.displayu.com/artwork/main.jpg",
                    true,
                    ArtworkImageType.ARTWORK,
                    0,
                    null,
                    1200,
                    800)));

    DisplayArtworkDetailResult result =
        DisplayArtworkDetailResult.of(artwork, "작가", 1L, List.of(), List.of(), 0L, false, false);

    assertThat(result.exhibitionInfo().exhibitionSubtitle()).isEqualTo("디자인학부 졸업전시");
    assertThat(result.exhibitionInfo().exhibitionOrganizer()).isEqualTo("디유대학교");
  }
}
