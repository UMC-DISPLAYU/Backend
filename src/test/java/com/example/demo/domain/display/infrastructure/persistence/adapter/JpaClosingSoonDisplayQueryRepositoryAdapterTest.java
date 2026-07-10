package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryRepository;
import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
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
@Import(JpaClosingSoonDisplayQueryRepositoryAdapter.class)
class JpaClosingSoonDisplayQueryRepositoryAdapterTest {

  @Autowired private ClosingSoonDisplayQueryRepository queryRepository;

  @Autowired private SpringDataDisplayJpaRepository jpaRepository;

  @Test
  void findClosingSoonDisplaysReturnsPublishedDisplaysOrderedByClosestEndDate() {
    LocalDate today = LocalDate.of(2026, 7, 11);
    Display endsInFiveDays = publishedDisplay("5일 남은 전시", today.minusDays(5), today.plusDays(5));
    Display endsToday = publishedDisplay("오늘 종료 전시", today.minusDays(3), today);
    Display draft = draftDisplay("초안 전시", today.minusDays(1), today.plusDays(1));
    Display ended = publishedDisplay("종료된 전시", today.minusDays(10), today.minusDays(1));
    jpaRepository.saveAllAndFlush(List.of(endsInFiveDays, endsToday, draft, ended));

    List<ClosingSoonDisplayQueryResult> results = queryRepository.findClosingSoonDisplays(today);

    assertThat(results)
        .extracting(ClosingSoonDisplayQueryResult::title)
        .containsExactly("오늘 종료 전시", "5일 남은 전시");
    assertThat(results.getFirst().posterImageUrl())
        .isEqualTo("https://cdn.displayu.com/posters/main.png");
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

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
