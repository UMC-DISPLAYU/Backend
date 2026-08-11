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
class ArtistDisplayControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private Clock clock;

  @Test
  void getArtistDisplaysReturnsOnlyPublishedCreatedAndAcceptedParticipatedDisplays()
      throws Exception {
    LocalDate today = LocalDate.now(clock);
    Display publishedCreated =
        published(display(1L, "공개 만든 전시", today.minusDays(1), today.plusDays(3)));
    Display draftCreated = display(1L, "초안 만든 전시", today.minusDays(1), today.plusDays(3));
    Display acceptedParticipated =
        published(
            participatedDisplay(2L, 1L, "공개 참여 전시", today.minusDays(10), today.minusDays(1), true));
    Display pendingParticipated =
        published(participatedDisplay(3L, 1L, "미수락 참여 전시", today, today.plusDays(5), false));
    Display selfParticipated =
        published(participatedDisplay(1L, 1L, "본인 만든 참여 전시", today, today.plusDays(5), true));
    displayJpaRepository.saveAllAndFlush(
        List.of(
            publishedCreated,
            draftCreated,
            acceptedParticipated,
            pendingParticipated,
            selfParticipated));

    mockMvc
        .perform(get("/api/v1/display/artists/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.createdDisplays.length()").value(2))
        .andExpect(jsonPath("$.success.data.createdDisplays[0].title").value("본인 만든 참여 전시"))
        .andExpect(jsonPath("$.success.data.createdDisplays[0].isDisplaying").value(true))
        .andExpect(
            jsonPath("$.success.data.createdDisplays[0].postImageUrl")
                .value("https://cdn.displayu.com/posters/main.png"))
        .andExpect(jsonPath("$.success.data.createdDisplays[1].title").value("공개 만든 전시"))
        .andExpect(jsonPath("$.success.data.participatedDisplays.length()").value(1))
        .andExpect(jsonPath("$.success.data.participatedDisplays[0].title").value("공개 참여 전시"))
        .andExpect(jsonPath("$.success.data.participatedDisplays[0].isDisplaying").value(false));
  }

  @Test
  void getArtistDisplaysReturnsEmptyListsWithoutAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/v1/display/artists/999"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.createdDisplays.length()").value(0))
        .andExpect(jsonPath("$.success.data.participatedDisplays.length()").value(0));
  }

  private static Display published(Display display) {
    display.publish();
    return display;
  }

  private static Display participatedDisplay(
      Long ownerUserId,
      Long participantUserId,
      String title,
      LocalDate startDate,
      LocalDate endDate,
      boolean accepted) {
    Display display = display(ownerUserId, title, startDate, endDate);
    display.addTeamMember(
        new TeamMember(
            null, new UserId(participantUserId), "참여 작가", TeamMemberRole.TEAM_MEM, accepted));
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
}
