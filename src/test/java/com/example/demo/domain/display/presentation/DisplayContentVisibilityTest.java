package com.example.demo.domain.display.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayStatus;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.global.security.JwtFactory;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayContentVisibilityTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private SpringDataDisplayJpaRepository displayRepository;
  @Autowired private JwtFactory jwtFactory;
  @MockitoBean private Clock clock;

  @BeforeEach
  void setClock() {
    when(clock.getZone()).thenReturn(ZoneId.of("Asia/Seoul"));
    when(clock.instant()).thenReturn(Instant.parse("2026-11-19T14:59:59Z"));
  }

  @ParameterizedTest
  @CsvSource({
    "IMMEDIATELY, IMMEDIATELY, true, true",
    "IMMEDIATELY, ON_EXHIBITION, true, false",
    "ON_EXHIBITION, IMMEDIATELY, false, true",
    "ON_EXHIBITION, ON_EXHIBITION, false, false"
  })
  void returnsIndependentTabVisibilityBeforeStartDate(
      ContentOpenPolicy artworkPolicy,
      ContentOpenPolicy exhibitionPolicy,
      boolean artworkVisible,
      boolean exhibitionVisible)
      throws Exception {
    Display display = display(artworkPolicy, exhibitionPolicy);
    display.publish();
    displayRepository.saveAndFlush(display);

    mockMvc
        .perform(get("/api/v1/display/{displayId}", display.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkContentVisible").value(artworkVisible))
        .andExpect(jsonPath("$.success.data.exhibitionContentVisible").value(exhibitionVisible))
        .andExpect(jsonPath("$.success.data.status").value("PUBLISHED"))
        .andExpect(jsonPath("$.success.data.artworkContentOpen").value(artworkPolicy.name()))
        .andExpect(jsonPath("$.success.data.exhibitionContentOpen").value(exhibitionPolicy.name()))
        .andExpect(jsonPath("$.success.data.period.startDate").value("2026-11-20"))
        .andExpect(jsonPath("$.success.data.period.endDate").value("2026-11-22"))
        .andExpect(jsonPath("$.success.data.period.startTime").value("10:00:00"))
        .andExpect(jsonPath("$.success.data.period.endTime").value("18:00:00"));
  }

  @Test
  void opensBothTabsAtSeoulMidnightBeforeDailyOpeningTime() throws Exception {
    Display display = display(ContentOpenPolicy.ON_EXHIBITION, ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    displayRepository.saveAndFlush(display);

    mockMvc
        .perform(get("/api/v1/display/{displayId}", display.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkContentVisible").value(false))
        .andExpect(jsonPath("$.success.data.exhibitionContentVisible").value(false));

    when(clock.instant()).thenReturn(Instant.parse("2026-11-19T15:00:00Z"));

    mockMvc
        .perform(get("/api/v1/display/{displayId}", display.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkContentVisible").value(true))
        .andExpect(jsonPath("$.success.data.exhibitionContentVisible").value(true));
  }

  @Test
  void keepsBothTabsVisibleAfterExhibitionEnds() throws Exception {
    when(clock.instant()).thenReturn(Instant.parse("2026-11-23T00:00:00Z"));
    Display display = display(ContentOpenPolicy.ON_EXHIBITION, ContentOpenPolicy.ON_EXHIBITION);
    display.publish();
    displayRepository.saveAndFlush(display);

    mockMvc
        .perform(get("/api/v1/display/{displayId}", display.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkContentVisible").value(true))
        .andExpect(jsonPath("$.success.data.exhibitionContentVisible").value(true));
  }

  @ParameterizedTest
  @EnumSource(
      value = DisplayStatus.class,
      names = {"DRAFT", "PENDING_REVIEW", "REJECTED"})
  void ownerPreviewDoesNotMakeUnpublishedTabsPublic(DisplayStatus displayStatus) throws Exception {
    when(clock.instant()).thenReturn(Instant.parse("2026-11-23T00:00:00Z"));
    Display display = display(ContentOpenPolicy.IMMEDIATELY, ContentOpenPolicy.ON_EXHIBITION);
    if (displayStatus != DisplayStatus.DRAFT) {
      display.requestReview();
    }
    if (displayStatus == DisplayStatus.REJECTED) {
      display.rejectReview();
    }
    displayRepository.saveAndFlush(display);

    mockMvc
        .perform(
            get("/api/v1/display/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.status").value(displayStatus.name()))
        .andExpect(jsonPath("$.success.data.artworkContentVisible").value(false))
        .andExpect(jsonPath("$.success.data.exhibitionContentVisible").value(false));
  }

  @Test
  void reservationResponseUsesSameVisibilityRuleAsDetail() throws Exception {
    Display display = display(ContentOpenPolicy.IMMEDIATELY, ContentOpenPolicy.IMMEDIATELY);
    display.publish();
    displayRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/reservation", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"artworkContentOpen":"ON_EXHIBITION","exhibitionContentOpen":"IMMEDIATELY"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.artworkContentVisible").value(false))
        .andExpect(jsonPath("$.success.data.exhibitionContentVisible").value(true));
  }

  private String bearer() {
    return "Bearer " + jwtFactory.create("1", 3_600_000L, "ACCESS");
  }

  private Display display(ContentOpenPolicy artworkPolicy, ContentOpenPolicy exhibitionPolicy) {
    Display display =
        Display.create(
            new UserId(1L),
            "공개 시점 전시",
            "https://cdn.displayu.com/poster.jpg",
            "전시 부제",
            "전시 설명",
            new DisplayLocation("전시장", BigDecimal.valueOf(37.5), BigDecimal.valueOf(127.0)),
            "",
            "전시 유의사항",
            "중앙대학교",
            "디자인학부",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 11, 20),
                LocalDate.of(2026, 11, 22),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            artworkPolicy,
            exhibitionPolicy);
    display.addTeamMember(
        new TeamMember(null, new UserId(1L), "팀장", TeamMemberRole.TEAM_LEADER, true));
    return display;
  }
}
