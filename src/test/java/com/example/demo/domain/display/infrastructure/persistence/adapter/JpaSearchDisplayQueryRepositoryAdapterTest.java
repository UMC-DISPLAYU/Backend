package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.SearchDisplayQuery;
import com.example.demo.domain.display.application.query.SearchDisplayQueryRepository;
import com.example.demo.domain.display.application.query.SearchDisplayQueryResult;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayLike;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplaySearchSort;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.SearchDisplayStatus;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.global.config.JpaAuditingConfig;
import com.example.demo.global.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
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
@Import({
  JpaSearchDisplayQueryRepositoryAdapter.class,
  JpaAuditingConfig.class,
  QuerydslConfig.class
})
class JpaSearchDisplayQueryRepositoryAdapterTest {

  @Autowired private SearchDisplayQueryRepository queryRepository;

  @Autowired private SpringDataDisplayJpaRepository jpaRepository;

  @Autowired private EntityManager entityManager;

  @Test
  void searchDisplaysFiltersPublishedDisplaysByTitleTypeFieldAndOngoingStatus() {
    LocalDate today = LocalDate.of(2026, 7, 12);
    Display first =
        publishedDisplay(
            "디자인 졸업전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(5));
    Display second =
        publishedDisplay(
            "시각 디자인 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(2),
            today.plusDays(3));
    Display differentField =
        publishedDisplay(
            "디자인 회화 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.PAINTING),
            today.minusDays(1),
            today.plusDays(2));
    Display differentKeyword =
        publishedDisplay(
            "회화 졸업전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(2));
    Display differentType =
        publishedDisplay(
            "디자인 동아리 전시",
            DisplayType.SMALL_GROUP,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(2));
    Display differentRegion =
        publishedDisplay(
            "디자인 경기 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(2),
            DisplayRegion.GYEONGGI_INCHEON);
    Display upcoming =
        publishedDisplay(
            "디자인 예정 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.plusDays(1),
            today.plusDays(10));
    Display draft =
        draftDisplay(
            "디자인 초안",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(5));
    Display deleted =
        publishedDisplay(
            "디자인 삭제 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(5));
    deleted.delete();
    jpaRepository.saveAllAndFlush(
        List.of(
            first,
            second,
            differentField,
            differentKeyword,
            differentType,
            differentRegion,
            upcoming,
            draft,
            deleted));

    List<SearchDisplayQueryResult> results =
        queryRepository.searchDisplays(
            new SearchDisplayQuery(
                "디자인",
                SearchDisplayStatus.ONGOING,
                DisplayRegion.SEOUL,
                DisplayField.DESIGN,
                DisplayType.GRADUATION,
                0L,
                20,
                null,
                null),
            today,
            20);

    assertThat(results)
        .extracting(SearchDisplayQueryResult::title)
        .containsExactly("시각 디자인 전시", "디자인 졸업전시")
        .doesNotContain(
            "디자인 회화 전시", "회화 졸업전시", "디자인 동아리 전시", "디자인 경기 전시", "디자인 예정 전시", "디자인 초안", "디자인 삭제 전시");
    assertThat(results.getFirst().posterImageUrl())
        .isEqualTo("https://cdn.displayu.com/posters/main.png");
    assertThat(results.getFirst().organization()).isEqualTo("organization");
    assertThat(results.getFirst().department()).isEqualTo("department");
  }

  @Test
  void searchDisplaysAppliesCursorAndLimitByDisplayIdDesc() {
    LocalDate today = LocalDate.of(2026, 7, 12);
    Display first =
        publishedDisplay(
            "첫 번째 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(5));
    Display second =
        publishedDisplay(
            "두 번째 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(5));
    Display third =
        publishedDisplay(
            "세 번째 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(5));
    jpaRepository.saveAllAndFlush(List.of(first, second, third));

    List<SearchDisplayQueryResult> firstPage =
        queryRepository.searchDisplays(
            new SearchDisplayQuery(null, null, null, null, null, 0L, 1, null, null), today, 1);
    List<SearchDisplayQueryResult> secondPage =
        queryRepository.searchDisplays(
            new SearchDisplayQuery(
                null, null, null, null, null, firstPage.getFirst().displayId(), 10, null, null),
            today,
            10);

    assertThat(firstPage).extracting(SearchDisplayQueryResult::title).containsExactly("세 번째 전시");
    assertThat(secondPage)
        .extracting(SearchDisplayQueryResult::title)
        .containsExactly("두 번째 전시", "첫 번째 전시");
  }

  @Test
  void searchDisplaysOmitsCursorConditionWhenCursorIsNull() {
    LocalDate today = LocalDate.of(2026, 7, 12);
    Display first =
        publishedDisplay(
            "첫 번째 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(5));
    Display second =
        publishedDisplay(
            "두 번째 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(5));
    jpaRepository.saveAllAndFlush(List.of(first, second));

    List<SearchDisplayQueryResult> results =
        queryRepository.searchDisplays(
            new SearchDisplayQuery(null, null, null, null, null, null, 20, null, null), today, 20);

    assertThat(results)
        .extracting(SearchDisplayQueryResult::title)
        .containsExactly("두 번째 전시", "첫 번째 전시");
  }

  @Test
  void searchDisplaysFiltersClosingSoonDisplaysEndingWithinThreeDays() {
    LocalDate today = LocalDate.of(2026, 7, 12);
    Display endingToday =
        publishedDisplay(
            "오늘 종료 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(5),
            today);
    Display endingInThreeDays =
        publishedDisplay(
            "3일 이내 종료 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(3));
    Display endingAfterThreeDays =
        publishedDisplay(
            "4일 뒤 종료 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(1),
            today.plusDays(4));
    Display upcomingEndingInThreeDays =
        publishedDisplay(
            "아직 시작 전인 3일 이내 종료 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.plusDays(1),
            today.plusDays(3));
    Display ended =
        publishedDisplay(
            "이미 종료 전시",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            today.minusDays(5),
            today.minusDays(1));
    jpaRepository.saveAllAndFlush(
        List.of(
            endingToday,
            endingInThreeDays,
            endingAfterThreeDays,
            upcomingEndingInThreeDays,
            ended));

    List<SearchDisplayQueryResult> results =
        queryRepository.searchDisplays(
            new SearchDisplayQuery(
                null, SearchDisplayStatus.CLOSING_SOON, null, null, null, 0L, 20, null, null),
            today,
            20);

    assertThat(results)
        .extracting(SearchDisplayQueryResult::title)
        .containsExactly("3일 이내 종료 전시", "오늘 종료 전시")
        .doesNotContain("4일 뒤 종료 전시", "아직 시작 전인 3일 이내 종료 전시", "이미 종료 전시");
  }

  @Test
  void searchesSchoolAndDepartmentWithoutTitleMatch() {
    LocalDate today = LocalDate.of(2026, 7, 12);
    Display display =
        publishedDisplay(
            "별빛", DisplayType.GRADUATION, List.of(DisplayField.GRAPHIC), today, today.plusDays(1));
    jpaRepository.saveAndFlush(display);
    for (String keyword : List.of("ORGANIZATION", "department")) {
      assertThat(
              queryRepository.searchDisplays(
                  new SearchDisplayQuery(
                      keyword,
                      SearchDisplayStatus.ONGOING,
                      null,
                      null,
                      null,
                      0L,
                      20,
                      DisplaySearchSort.LATEST,
                      null),
                  today,
                  20))
          .extracting(SearchDisplayQueryResult::displayId)
          .containsExactly(display.getId());
    }
  }

  @Test
  void likesCursorPreservesTiesZeroLikesAndIgnoresCancelledLikes() {
    LocalDate today = LocalDate.of(2026, 7, 12);
    Display first =
        publishedDisplay(
            "first",
            DisplayType.GRADUATION,
            List.of(DisplayField.GRAPHIC, DisplayField.PAINTING),
            today,
            today.plusDays(1));
    Display second =
        publishedDisplay(
            "second",
            DisplayType.GRADUATION,
            List.of(DisplayField.GRAPHIC),
            today,
            today.plusDays(1));
    Display third =
        publishedDisplay(
            "third",
            DisplayType.GRADUATION,
            List.of(DisplayField.GRAPHIC),
            today,
            today.plusDays(1));
    Display fourth =
        publishedDisplay(
            "fourth",
            DisplayType.GRADUATION,
            List.of(DisplayField.GRAPHIC),
            today,
            today.plusDays(1));
    jpaRepository.saveAllAndFlush(List.of(first, second, third, fourth));
    entityManager.persist(DisplayLike.create(first.getId(), new UserId(1L)));
    entityManager.persist(DisplayLike.create(first.getId(), new UserId(2L)));
    entityManager.persist(DisplayLike.create(second.getId(), new UserId(1L)));
    entityManager.persist(DisplayLike.create(second.getId(), new UserId(2L)));
    DisplayLike cancelled = DisplayLike.create(fourth.getId(), new UserId(1L));
    cancelled.cancel();
    entityManager.persist(cancelled);
    entityManager.flush();
    var firstPage =
        queryRepository.searchDisplays(
            new SearchDisplayQuery(
                null, null, null, DisplayField.GRAPHIC, null, 0L, 2, DisplaySearchSort.LIKES, null),
            today,
            2);
    assertThat(firstPage)
        .extracting(SearchDisplayQueryResult::displayId)
        .containsExactly(second.getId(), first.getId());
    assertThat(firstPage).extracting(SearchDisplayQueryResult::likeCount).containsExactly(2L, 2L);
    var secondPage =
        queryRepository.searchDisplays(
            new SearchDisplayQuery(
                null,
                null,
                null,
                DisplayField.GRAPHIC,
                null,
                first.getId(),
                2,
                DisplaySearchSort.LIKES,
                2L),
            today,
            2);
    assertThat(secondPage)
        .extracting(SearchDisplayQueryResult::displayId)
        .containsExactly(fourth.getId(), third.getId());
    assertThat(secondPage).extracting(SearchDisplayQueryResult::likeCount).containsExactly(0L, 0L);
    var afterTie =
        queryRepository.searchDisplays(
            new SearchDisplayQuery(
                null, null, null, null, null, second.getId(), 10, DisplaySearchSort.LIKES, 2L),
            today,
            10);
    assertThat(afterTie)
        .extracting(SearchDisplayQueryResult::displayId)
        .containsExactly(first.getId(), fourth.getId(), third.getId());
  }

  private static Display publishedDisplay(
      String title,
      DisplayType displayType,
      List<DisplayField> displayFields,
      LocalDate startDate,
      LocalDate endDate) {
    return publishedDisplay(
        title, displayType, displayFields, startDate, endDate, DisplayRegion.SEOUL);
  }

  private static Display publishedDisplay(
      String title,
      DisplayType displayType,
      List<DisplayField> displayFields,
      LocalDate startDate,
      LocalDate endDate,
      DisplayRegion region) {
    Display display = draftDisplay(title, displayType, displayFields, startDate, endDate);
    display.changeRegion(region);
    display.publish();
    return display;
  }

  private static Display draftDisplay(
      String title,
      DisplayType displayType,
      List<DisplayField> displayFields,
      LocalDate startDate,
      LocalDate endDate) {
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
        displayType,
        displayFields,
        new DisplayPeriod(startDate, endDate, LocalTime.of(10, 0), LocalTime.of(18, 0)),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
