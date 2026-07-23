package com.example.demo.domain.display.presentation;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayControllerClosingSoonTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository jpaRepository;

  @Autowired private Clock clock;

  @Test
  void getClosingSoonDisplaysReturnsCommonSuccessResponse() throws Exception {
    LocalDate today = LocalDate.now(clock);
    jpaRepository.saveAndFlush(publishedDisplay("마감 임박 전시", today.minusDays(3), today.plusDays(2)));
    jpaRepository.saveAndFlush(publishedDisplay("다음 전시", today.minusDays(2), today.plusDays(3)));

    mockMvc
        .perform(get("/api/v1/display/closing-soon?size=1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.exhibitions[0].displayId", notNullValue()))
        .andExpect(jsonPath("$.success.data.exhibitions[0].title").value("마감 임박 전시"))
        .andExpect(
            jsonPath("$.success.data.exhibitions[0].posterImageUrl")
                .value("https://cdn.displayu.com/posters/main.png"))
        .andExpect(jsonPath("$.success.data.exhibitions[0].organization").value("organization"))
        .andExpect(jsonPath("$.success.data.exhibitions[0].department").value("department"))
        .andExpect(
            jsonPath("$.success.data.exhibitions[0].startedAt")
                .value(today.minusDays(3).toString()))
        .andExpect(
            jsonPath("$.success.data.exhibitions[0].endedAt").value(today.plusDays(2).toString()))
        .andExpect(jsonPath("$.success.data.exhibitions[0].dayLeft").value(2))
        .andExpect(jsonPath("$.success.data.exhibitions[0].isBookmarked").doesNotExist())
        .andExpect(
            jsonPath("$.success.data.pagination.nextCursor")
                .value(startsWith(today.plusDays(2) + ":")))
        .andExpect(jsonPath("$.success.data.pagination.size").value(1))
        .andExpect(jsonPath("$.success.data.pagination.hasNext").value(true))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display/closing-soon"));
  }

  @Test
  void getClosingSoonDisplaysReturnsBadRequestWhenSizeIsInvalid() throws Exception {
    mockMvc
        .perform(get("/api/v1/display/closing-soon?size=101"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display/closing-soon"));
  }

  @Test
  void getClosingSoonDisplaysReturnsBadRequestWhenCursorFormatIsInvalid() throws Exception {
    mockMvc
        .perform(get("/api/v1/display/closing-soon?cursor=invalid-cursor"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display/closing-soon"));
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
    display.publish();
    return display;
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
