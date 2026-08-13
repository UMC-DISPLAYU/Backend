package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQuery;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQuery.Cursor;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryRepository;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.global.config.JpaAuditingConfig;
import com.example.demo.global.config.QuerydslConfig;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import({
  JpaClosingSoonDisplayQueryRepositoryAdapter.class,
  JpaAuditingConfig.class,
  QuerydslConfig.class
})
class JpaClosingSoonDisplayQueryRepositoryAdapterTest {

  @Autowired private ClosingSoonDisplayQueryRepository queryRepository;

  @Autowired private SpringDataDisplayJpaRepository jpaRepository;

  @Test
  void findClosingSoonDisplaysReturnsPublishedDisplaysOrderedByEndDateAsc() {
    LocalDate today = LocalDate.of(2026, 7, 11);
    Display first = publishedDisplay("첫 번째 전시", today.minusDays(5), today.plusDays(5));
    Display second = publishedDisplay("두 번째 전시", today.minusDays(3), today);
    Display draft = draftDisplay("초안 전시", today.minusDays(1), today.plusDays(1));
    Display ended = publishedDisplay("종료된 전시", today.minusDays(10), today.minusDays(1));
    Display deleted = publishedDisplay("삭제된 전시", today.minusDays(1), today.plusDays(1));
    deleted.delete();
    jpaRepository.saveAllAndFlush(List.of(first, second, draft, ended, deleted));

    List<ClosingSoonDisplayQueryResult> results =
        queryRepository.findClosingSoonDisplays(new ClosingSoonDisplayQuery(null, 20), today, 20);

    assertThat(results)
        .extracting(ClosingSoonDisplayQueryResult::title)
        .containsExactly("두 번째 전시", "첫 번째 전시")
        .doesNotContain("삭제된 전시");
    assertThat(results.getFirst().posterImageUrl())
        .isEqualTo("https://cdn.displayu.com/posters/main.png");
  }

  @Test
  void findClosingSoonDisplaysAppliesCursorAndLimit() {
    LocalDate today = LocalDate.of(2026, 7, 11);
    Display first = publishedDisplay("첫 번째 전시", today.minusDays(5), today.plusDays(5));
    Display second = publishedDisplay("두 번째 전시", today.minusDays(3), today);
    Display third = publishedDisplay("세 번째 전시", today.minusDays(3), today.plusDays(3));
    jpaRepository.saveAllAndFlush(List.of(first, second, third));

    List<ClosingSoonDisplayQueryResult> firstPage =
        queryRepository.findClosingSoonDisplays(new ClosingSoonDisplayQuery(null, 1), today, 1);
    List<ClosingSoonDisplayQueryResult> secondPage =
        queryRepository.findClosingSoonDisplays(
            new ClosingSoonDisplayQuery(
                new Cursor(firstPage.getFirst().endedAt(), firstPage.getFirst().displayId()), 10),
            today,
            10);

    assertThat(firstPage)
        .extracting(ClosingSoonDisplayQueryResult::title)
        .containsExactly("두 번째 전시");
    assertThat(secondPage)
        .extracting(ClosingSoonDisplayQueryResult::title)
        .containsExactly("세 번째 전시", "첫 번째 전시");
  }

  @Test
  void findClosingSoonDisplaysReturnsOnlyValidRepresentativeImage() {
    LocalDate today = LocalDate.of(2026, 7, 11);
    Display display = publishedDisplay("대표 이미지 조건 전시", today.minusDays(1), today.plusDays(1));
    display.addImage(
        image("https://cdn.displayu.com/posters/detail.png", DisplayImageType.DETAIL, 0, null));
    display.addImage(
        image(
            "https://cdn.displayu.com/posters/deleted-main.png",
            DisplayImageType.MAIN,
            0,
            LocalDateTime.of(2026, 7, 10, 12, 0)));
    display.addImage(
        image("https://cdn.displayu.com/posters/sorted-main.png", DisplayImageType.MAIN, 1, null));
    jpaRepository.saveAndFlush(display);

    List<ClosingSoonDisplayQueryResult> results =
        queryRepository.findClosingSoonDisplays(new ClosingSoonDisplayQuery(null, 20), today, 20);

    assertThat(results)
        .extracting(ClosingSoonDisplayQueryResult::posterImageUrl)
        .containsExactly("https://cdn.displayu.com/posters/main.png");
  }

  private static Display publishedDisplay(String title, LocalDate startDate, LocalDate endDate) {
    Display display = draftDisplay(title, startDate, endDate);
    display.publish();
    return display;
  }

  private static Display draftDisplay(String title, LocalDate startDate, LocalDate endDate) {
    return Display.create(
        new UserId(1L),
        title,
        "https://cdn.displayu.com/posters/main.png",
        "subtitle",
        "content",
        new DisplayLocation("전시장", bd("37.5513"), bd("126.9248")),
        "",
        "",
        "organization",
        "department",
        DisplayType.GRADUATION,
        List.of(DisplayField.DESIGN),
        new DisplayPeriod(startDate, endDate, LocalTime.of(10, 0), LocalTime.of(18, 0)),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }

  private static DisplayImage image(
      String imageUrl, DisplayImageType imageType, int sortOrder, LocalDateTime deletedAt) {
    return new DisplayImage(null, imageUrl, imageType, sortOrder, deletedAt);
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
