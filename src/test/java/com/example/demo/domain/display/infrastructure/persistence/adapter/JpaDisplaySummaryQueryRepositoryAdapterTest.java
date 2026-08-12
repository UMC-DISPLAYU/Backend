package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.DisplaySummaryQueryRepository;
import com.example.demo.domain.display.application.query.DisplaySummaryQueryResult;
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
  JpaDisplaySummaryQueryRepositoryAdapter.class,
  JpaAuditingConfig.class,
  QuerydslConfig.class
})
class JpaDisplaySummaryQueryRepositoryAdapterTest {

  @Autowired private DisplaySummaryQueryRepository queryRepository;

  @Autowired private SpringDataDisplayJpaRepository jpaRepository;

  @Test
  void findByDisplayIdInReturnsDisplaySummaries() {
    Display first =
        display("첫 번째 전시", "첫 번째 장소", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 10));
    Display second =
        display("두 번째 전시", "두 번째 장소", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 10));
    jpaRepository.saveAllAndFlush(List.of(first, second));

    List<DisplaySummaryQueryResult> results =
        queryRepository.findByDisplayIdIn(List.of(first.getId(), second.getId(), 999L));

    assertThat(results)
        .extracting(DisplaySummaryQueryResult::displayId)
        .containsExactlyInAnyOrder(first.getId(), second.getId());
    assertThat(results)
        .extracting(DisplaySummaryQueryResult::title)
        .containsExactlyInAnyOrder("첫 번째 전시", "두 번째 전시");
    assertThat(results)
        .extracting(DisplaySummaryQueryResult::posterImageUrl)
        .containsOnly("https://cdn.displayu.com/posters/main.png");
  }

  @Test
  void findByDisplayIdInUsesOnlyActiveMainImageWithSortOrderZero() {
    Display display =
        display("대표 이미지 전시", "전시장", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 10));
    display.addImage(
        image("https://cdn.displayu.com/posters/detail.png", DisplayImageType.DETAIL, 0, null));
    display.addImage(
        image(
            "https://cdn.displayu.com/posters/deleted-main.png",
            DisplayImageType.MAIN,
            0,
            LocalDateTime.of(2026, 7, 1, 12, 0)));
    display.addImage(
        image("https://cdn.displayu.com/posters/sorted-main.png", DisplayImageType.MAIN, 1, null));
    jpaRepository.saveAndFlush(display);

    List<DisplaySummaryQueryResult> results =
        queryRepository.findByDisplayIdIn(List.of(display.getId()));

    assertThat(results).hasSize(1);
    assertThat(results.getFirst().posterImageUrl())
        .isEqualTo("https://cdn.displayu.com/posters/main.png");
  }

  @Test
  void findByDisplayIdInReturnsOnlyPublishedDisplays() {
    Display published =
        display("공개 전시", "전시장", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 10));
    Display draft =
        draftDisplay("초안 전시", "전시장", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 10));
    Display deleted = display("삭제 전시", "전시장", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 10));
    deleted.delete();
    jpaRepository.saveAllAndFlush(List.of(published, draft, deleted));

    List<DisplaySummaryQueryResult> results =
        queryRepository.findByDisplayIdIn(
            List.of(published.getId(), draft.getId(), deleted.getId()));

    assertThat(results)
        .extracting(DisplaySummaryQueryResult::displayId)
        .containsExactly(published.getId());
  }

  @Test
  void findByDisplayIdInReturnsEmptyWhenIdsAreEmpty() {
    List<DisplaySummaryQueryResult> results = queryRepository.findByDisplayIdIn(List.of());

    assertThat(results).isEmpty();
  }

  private static Display display(
      String title, String placeName, LocalDate startDate, LocalDate endDate) {
    Display display = draftDisplay(title, placeName, startDate, endDate);
    display.publish();
    return display;
  }

  private static Display draftDisplay(
      String title, String placeName, LocalDate startDate, LocalDate endDate) {
    return Display.create(
        new UserId(1L),
        title,
        "https://cdn.displayu.com/posters/main.png",
        "subtitle",
        "content",
        new DisplayLocation(placeName, bd("37.5513"), bd("126.9248")),
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
