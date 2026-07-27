package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.domain.display.application.query.ClosingSoonDisplayQueryResult;
import com.example.demo.domain.display.application.query.GraduationDisplayQueryRepository;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.global.config.JpaAuditingConfig;
import com.example.demo.global.config.QuerydslConfig;
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
  JpaGraduationDisplayQueryRepositoryAdapter.class,
  JpaAuditingConfig.class,
  QuerydslConfig.class
})
class JpaGraduationDisplayQueryRepositoryAdapterTest {

  @Autowired private GraduationDisplayQueryRepository queryRepository;

  @Autowired private SpringDataDisplayJpaRepository jpaRepository;

  @Test
  void findRandomGraduationDisplaysReturnsPublishedGraduationDisplaysWithinSize() {
    Display first = publishedDisplay("졸업 전시 1", DisplayType.GRADUATION);
    Display second = publishedDisplay("졸업 전시 2", DisplayType.GRADUATION);
    Display third = publishedDisplay("졸업 전시 3", DisplayType.GRADUATION);
    Display assignment = publishedDisplay("과제 전시", DisplayType.ASSIGNMENTS);
    Display draft = draftDisplay("초안 졸업 전시", DisplayType.GRADUATION);
    jpaRepository.saveAllAndFlush(List.of(first, second, third, assignment, draft));

    List<ClosingSoonDisplayQueryResult> results = queryRepository.findRandomGraduationDisplays(2);

    assertThat(results).hasSize(2);
    assertThat(results)
        .extracting(ClosingSoonDisplayQueryResult::title)
        .allMatch(title -> title.startsWith("졸업 전시"));
    assertThat(results)
        .extracting(ClosingSoonDisplayQueryResult::posterImageUrl)
        .containsOnly("https://cdn.displayu.com/posters/main.png");
  }

  private static Display publishedDisplay(String title, DisplayType displayType) {
    Display display = draftDisplay(title, displayType);
    display.publish();
    return display;
  }

  private static Display draftDisplay(String title, DisplayType displayType) {
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
