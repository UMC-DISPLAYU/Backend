package com.example.demo.domain.display.presentation;

import static org.hamcrest.Matchers.notNullValue;
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
class DisplayControllerMapTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository jpaRepository;

  @Autowired private Clock clock;

  @Test
  void getDisplayMapReturnsMarkersWithCommonSuccessResponse() throws Exception {
    jpaRepository.saveAndFlush(publishedDisplay("내면의 풍경", "홍익대학교 현대미술관"));

    mockMvc
        .perform(
            get("/api/v1/display/map")
                .param("southLatitude", "37.4900")
                .param("westLongitude", "126.9000")
                .param("northLatitude", "37.5700")
                .param("eastLongitude", "127.0000")
                .param("searchWord", " 홍익 ")
                .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.markers[0].displayId", notNullValue()))
        .andExpect(jsonPath("$.success.data.markers[0].title").value("내면의 풍경"))
        .andExpect(
            jsonPath("$.success.data.markers[0].startDate").value(LocalDate.now(clock).toString()))
        .andExpect(
            jsonPath("$.success.data.markers[0].endDate")
                .value(LocalDate.now(clock).plusDays(8).toString()))
        .andExpect(jsonPath("$.success.data.markers[0].locationName").value("홍익대학교 현대미술관"))
        .andExpect(
            jsonPath("$.success.data.markers[0].posterImageUrl")
                .value("https://cdn.displayu.com/posters/main.png"))
        .andExpect(
            jsonPath("$.success.data.markers[0].schoolDepartmentName")
                .value("organization department"))
        .andExpect(jsonPath("$.success.data.markers[0].isArchived").value(false))
        .andExpect(jsonPath("$.success.data.pagination.nextCursor").doesNotExist())
        .andExpect(jsonPath("$.success.data.pagination.size").value(20))
        .andExpect(jsonPath("$.success.data.pagination.hasNext").value(false))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display/map"));
  }

  @Test
  void getDisplayMapReturnsBadRequestWhenBoundsAreReversed() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/display/map")
                .param("southLatitude", "37.5700")
                .param("westLongitude", "126.9000")
                .param("northLatitude", "37.4900")
                .param("eastLongitude", "127.0000"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display/map"));
  }

  @Test
  void getDisplayMapReturnsBadRequestWhenSizeIsInvalid() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/display/map")
                .param("southLatitude", "37.4900")
                .param("westLongitude", "126.9000")
                .param("northLatitude", "37.5700")
                .param("eastLongitude", "127.0000")
                .param("size", "101"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/display/map"));
  }

  @Test
  void allModeReturnsMoreThanOneHundredMarkersAndIgnoresSize() throws Exception {
    for (int index = 0; index < 101; index++) {
      jpaRepository.save(publishedDisplay("전시 " + index, "gallery"));
    }
    jpaRepository.flush();
    mockMvc
        .perform(
            get("/api/v1/display/map")
                .param("southLatitude", "37.4900")
                .param("westLongitude", "126.9000")
                .param("northLatitude", "37.5700")
                .param("eastLongitude", "127.0000")
                .param("searchWord", " department ")
                .param("all", "true")
                .param("size", "1000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.markers.length()").value(101))
        .andExpect(jsonPath("$.success.data.pagination.size").value(101))
        .andExpect(jsonPath("$.success.data.pagination.hasNext").value(false))
        .andExpect(jsonPath("$.success.data.pagination.nextCursor").doesNotExist());
  }

  @Test
  void allModeRejectsCursorAndReturnsEmptyLastPageWithoutMatches() throws Exception {
    mockMvc
        .perform(
            get("/api/v1/display/map")
                .param("southLatitude", "37.4900")
                .param("westLongitude", "126.9000")
                .param("northLatitude", "37.5700")
                .param("eastLongitude", "127.0000")
                .param("all", "true")
                .param("cursor", "1"))
        .andExpect(status().isBadRequest());
    mockMvc
        .perform(
            get("/api/v1/display/map")
                .param("southLatitude", "37.4900")
                .param("westLongitude", "126.9000")
                .param("northLatitude", "37.5700")
                .param("eastLongitude", "127.0000")
                .param("all", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.markers.length()").value(0))
        .andExpect(jsonPath("$.success.data.pagination.size").value(0))
        .andExpect(jsonPath("$.success.data.pagination.hasNext").value(false));
  }

  private Display publishedDisplay(String title, String placeName) {
    Display display =
        Display.create(
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
            new DisplayPeriod(
                LocalDate.now(clock),
                LocalDate.now(clock).plusDays(8),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    return display;
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }
}
