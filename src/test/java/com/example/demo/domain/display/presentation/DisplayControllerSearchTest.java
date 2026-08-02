package com.example.demo.domain.display.presentation;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.archive.domain.aggregate.ArchiveDisplay;
import com.example.demo.domain.archive.infrastructure.persistence.SpringDataArchiveDisplayJpaRepository;
import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.global.security.JwtFactory;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayControllerSearchTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository jpaRepository;

  @Autowired private SpringDataArchiveDisplayJpaRepository archiveDisplayJpaRepository;

  @Autowired private JwtFactory jwtFactory;

  @Autowired private Clock clock;

  @Test
  void searchDisplaysReturnsCommonSuccessResponse() throws Exception {
    LocalDate today = LocalDate.now(clock);
    jpaRepository.saveAndFlush(publishedDisplay("디자인 졸업전시", today.minusDays(1), today.plusDays(5)));
    jpaRepository.saveAndFlush(
        publishedDisplay("시각 디자인 전시", today.minusDays(2), today.plusDays(3)));

    mockMvc
        .perform(
            get("/api/v1/display/search")
                .param("searchWord", " 디자인 ")
                .param("status", "ONGOING")
                .param("region", "SEOUL")
                .param("field", "DESIGN")
                .param("type", "GRADUATION")
                .param("cursor", "0")
                .param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.exhibitions[0].displayId", notNullValue()))
        .andExpect(jsonPath("$.success.data.exhibitions[0].title").value("디자인 졸업전시"))
        .andExpect(
            jsonPath("$.success.data.exhibitions[0].posterImageUrl")
                .value("https://cdn.displayu.com/posters/main.png"))
        .andExpect(
            jsonPath("$.success.data.exhibitions[0].startedAt")
                .value(today.minusDays(1).toString()))
        .andExpect(
            jsonPath("$.success.data.exhibitions[0].endedAt").value(today.plusDays(5).toString()))
        .andExpect(jsonPath("$.success.data.exhibitions[0].dayLeft").value(5))
        .andExpect(jsonPath("$.success.data.exhibitions[0].isBookmarked").value(false))
        .andExpect(jsonPath("$.success.data.pagination.nextCursor", notNullValue()))
        .andExpect(jsonPath("$.success.data.pagination.size").value(1))
        .andExpect(jsonPath("$.success.data.pagination.hasNext").value(true))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display/search"));
  }

  @Test
  void searchDisplaysReturnsBookmarkedTrueWhenRequesterArchivedDisplay() throws Exception {
    LocalDate today = LocalDate.now(clock);
    Display display =
        jpaRepository.saveAndFlush(
            publishedDisplay("디자인 졸업전시", today.minusDays(1), today.plusDays(5)));
    archiveDisplayJpaRepository.saveAndFlush(ArchiveDisplay.create(display.getId(), 7L));

    mockMvc
        .perform(
            get("/api/v1/display/search")
                .header(HttpHeaders.AUTHORIZATION, bearer(7L))
                .param("searchWord", "디자인")
                .param("cursor", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.exhibitions[0].displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.exhibitions[0].isBookmarked").value(true));
  }

  @Test
  void searchDisplaysReturnsBadRequestWhenRequiredPaginationIsMissing() throws Exception {
    mockMvc
        .perform(get("/api/v1/display/search").param("size", "10"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display/search"));
  }

  private static Display publishedDisplay(String title, LocalDate startDate, LocalDate endDate) {
    Display display =
        Display.create(
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
    display.changeRegion(DisplayRegion.SEOUL);
    display.publish();
    return display;
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }
}
