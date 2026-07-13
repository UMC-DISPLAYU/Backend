package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.DisplayMapQuery;
import com.example.demo.domain.display.application.query.DisplayMapQueryRepository;
import com.example.demo.domain.display.application.query.DisplayMapQueryResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaDisplayMapQueryRepositoryAdapter.class)
class JpaDisplayMapQueryRepositoryAdapterTest {

  @Autowired private DisplayMapQueryRepository displayMapQueryRepository;

  @Autowired private SpringDataDisplayJpaRepository jpaRepository;

  @Test
  void findMarkersFiltersAndSortsByDisplayIdDescWithProjection() {
    Display first = publishedDisplay("디자인 전시", "홍익대학교 현대미술관", "37.5513", "126.9248");
    Display second = publishedDisplay("내면의 풍경", "서울 갤러리", "37.5600", "126.9300");
    Display draft = draftDisplay("디자인 초안", "홍익대학교", "37.5520", "126.9250");
    Display outside = publishedDisplay("영역 밖 전시", "부산 갤러리", "35.1000", "129.0300");
    jpaRepository.saveAllAndFlush(List.of(first, second, draft, outside));

    List<DisplayMapQueryResult> results =
        displayMapQueryRepository.findMarkers(
            query(bd("37.4900"), bd("126.9000"), bd("37.5700"), bd("127.0000"), null, null), 10);

    assertThat(results)
        .extracting(DisplayMapQueryResult::title)
        .containsExactly("내면의 풍경", "디자인 전시");
    assertThat(results.getFirst().posterImageUrl())
        .isEqualTo("https://cdn.displayu.com/posters/main.png");
  }

  @Test
  void findMarkersAppliesSearchWordAndCursor() {
    Display first = publishedDisplay("디자인 전시", "홍익대학교 현대미술관", "37.5513", "126.9248");
    Display second = publishedDisplay("내면의 풍경", "서울 디자인 센터", "37.5600", "126.9300");
    Display third = publishedDisplay("졸업 전시", "서울 갤러리", "37.5650", "126.9400");
    jpaRepository.saveAllAndFlush(List.of(first, second, third));

    DisplayMapQuery firstPageQuery =
        query(bd("37.4900"), bd("126.9000"), bd("37.5700"), bd("127.0000"), "디자인", null);
    List<DisplayMapQueryResult> firstPage =
        displayMapQueryRepository.findMarkers(firstPageQuery, 1);

    DisplayMapQuery secondPageQuery =
        query(
            bd("37.4900"),
            bd("126.9000"),
            bd("37.5700"),
            bd("127.0000"),
            "디자인",
            firstPage.getFirst().displayId());
    List<DisplayMapQueryResult> secondPage =
        displayMapQueryRepository.findMarkers(secondPageQuery, 10);

    assertThat(firstPage).extracting(DisplayMapQueryResult::title).containsExactly("내면의 풍경");
    assertThat(secondPage).extracting(DisplayMapQueryResult::title).containsExactly("디자인 전시");
  }

  private static DisplayMapQuery query(
      BigDecimal southLatitude,
      BigDecimal westLongitude,
      BigDecimal northLatitude,
      BigDecimal eastLongitude,
      String searchWord,
      Long cursor) {
    return new DisplayMapQuery(
        southLatitude, westLongitude, northLatitude, eastLongitude, searchWord, cursor, 20);
  }

  private static Display publishedDisplay(
      String title, String placeName, String latitude, String longitude) {
    Display display = draftDisplay(title, placeName, latitude, longitude);
    display.publish();
    return display;
  }

  private static Display draftDisplay(
      String title, String placeName, String latitude, String longitude) {
    return Display.create(
        new UserId(1L),
        title,
        "https://cdn.displayu.com/posters/main.png",
        "subtitle",
        "content",
        new DisplayLocation(placeName, bd(latitude), bd(longitude)),
        "",
        "",
        "organization",
        "department",
        DisplayType.GRADUATION,
        List.of(DisplayField.DESIGN),
        new DisplayPeriod(
            LocalDate.of(2026, 5, 20),
            LocalDate.of(2026, 5, 28),
            LocalTime.of(10, 0),
            LocalTime.of(18, 0)),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
