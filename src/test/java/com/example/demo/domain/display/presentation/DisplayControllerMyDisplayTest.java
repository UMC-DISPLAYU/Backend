package com.example.demo.domain.display.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
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
class DisplayControllerMyDisplayTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private JwtFactory jwtFactory;

  @Autowired private Clock clock;

  @Test
  void getMyDisplaysReturnsCreatedAndParticipatedDisplays() throws Exception {
    LocalDate today = LocalDate.now(clock);
    Display createdDisplay =
        displayJpaRepository.saveAndFlush(
            display(1L, "내가 만든 전시", today.minusDays(1), today.plusDays(3)));
    Display participatedDisplay =
        displayJpaRepository.saveAndFlush(
            participatedDisplay(2L, 1L, "내가 참여한 전시", today.minusDays(10), today.minusDays(1)));

    mockMvc
        .perform(get("/api/v1/display/me").header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(
            jsonPath("$.success.data.createdDisplays[0].displayId").value(createdDisplay.getId()))
        .andExpect(jsonPath("$.success.data.createdDisplays[0].title").value("내가 만든 전시"))
        .andExpect(jsonPath("$.success.data.createdDisplays[0].displayStatus").value("DISPLAYING"))
        .andExpect(
            jsonPath("$.success.data.createdDisplays[0].startDate")
                .value(today.minusDays(1).toString()))
        .andExpect(
            jsonPath("$.success.data.createdDisplays[0].endDate")
                .value(today.plusDays(3).toString()))
        .andExpect(jsonPath("$.success.data.createdDisplays[0].school").value("디유대학교"))
        .andExpect(jsonPath("$.success.data.createdDisplays[0].department").value("디자인학부"))
        .andExpect(jsonPath("$.success.data.createdDisplays[0].placeName").value("디유 갤러리"))
        .andExpect(
            jsonPath("$.success.data.createdDisplays[0].postImageUrl")
                .value("https://cdn.displayu.com/posters/main.png"))
        .andExpect(
            jsonPath("$.success.data.participatedDisplays[0].displayId")
                .value(participatedDisplay.getId()))
        .andExpect(jsonPath("$.success.data.participatedDisplays[0].title").value("내가 참여한 전시"))
        .andExpect(jsonPath("$.success.data.participatedDisplays[0].displayStatus").value("ENDED"));
  }

  @Test
  void getMyDisplaysReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/v1/display/me"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  private static Display participatedDisplay(
      Long ownerUserId,
      Long participantUserId,
      String title,
      LocalDate startDate,
      LocalDate endDate) {
    Display display = display(ownerUserId, title, startDate, endDate);
    display.addTeamMember(
        new TeamMember(
            null, new UserId(participantUserId), "참여 작가", TeamMemberRole.TEAM_MEM, true));
    return display;
  }

  private static Display display(
      Long ownerUserId, String title, LocalDate startDate, LocalDate endDate) {
    return Display.create(
        new UserId(ownerUserId),
        title,
        "https://cdn.displayu.com/posters/main.png",
        "subtitle",
        "content",
        new DisplayLocation("디유 갤러리", bd("37.5513"), bd("126.9248")),
        "",
        "",
        "디유대학교",
        "디자인학부",
        DisplayType.GRADUATION,
        List.of(DisplayField.DESIGN),
        new DisplayPeriod(startDate, endDate, LocalTime.of(10, 0), LocalTime.of(18, 0)),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }
}
