package com.example.demo.domain.display.infrastructure.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.test.context.ActiveProfiles;

@org.springframework.boot.test.context.SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
@ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
class PopularDisplaysTest {
  @Autowired
  private com.example.demo.domain.display.application.query.PopularDisplayQueryRepository
      repository;

  @Autowired private SpringDataDisplayJpaRepository jpaRepository;
  @Autowired private jakarta.persistence.EntityManager em;
  @Autowired private org.springframework.test.web.servlet.MockMvc mvc;

  @Test
  void ranksByCumulativeLikesAndIdAndLimitsToTen() throws Exception {
    java.util.ArrayList<Display> displays = new java.util.ArrayList<>();
    for (int i = 0; i < 12; i++)
      displays.add(jpaRepository.saveAndFlush(publishedDisplay("전시" + i, DisplayType.GRADUATION)));
    for (int i = 0; i < 3; i++)
      em.persist(
          com.example.demo.domain.display.domain.entity.DisplayLike.create(
              displays.get(0).getId(), new UserId((long) i + 100)));
    em.persist(
        com.example.demo.domain.display.domain.entity.DisplayLike.create(
            displays.get(1).getId(), new UserId(100L)));
    Display draft = jpaRepository.saveAndFlush(draftDisplay("초안", DisplayType.GRADUATION));
    Display deleted = publishedDisplay("삭제", DisplayType.GRADUATION);
    deleted.delete();
    jpaRepository.saveAndFlush(deleted);
    em.flush();
    em.clear();
    var results = repository.findPopularDisplays(LocalDate.now(java.time.ZoneId.of("Asia/Seoul")));
    assertThat(results).hasSize(10);
    assertThat(results)
        .extracting(ClosingSoonDisplayQueryResult::displayId)
        .startsWith(displays.get(0).getId(), displays.get(1).getId(), displays.get(11).getId());
    assertThat(results).extracting(ClosingSoonDisplayQueryResult::title).doesNotContain("초안", "삭제");
    assertThat(
            repository.findPopularDisplays(
                LocalDate.now(java.time.ZoneId.of("Asia/Seoul")).plusDays(4)))
        .isEmpty();
    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(
                "/api/v1/display/popular"))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.exhibitions.length()")
                .value(10))
        .andExpect(
            org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath(
                    "$.success.data.exhibitions[0].placeName")
                .value("전시장"));
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
            LocalDate.now(java.time.ZoneId.of("Asia/Seoul")),
            LocalDate.now(java.time.ZoneId.of("Asia/Seoul")).plusDays(3),
            LocalTime.of(10, 0),
            LocalTime.of(18, 0)),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
