package com.example.demo.domain.display.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayScreeningJpaRepository;
import com.example.demo.global.security.JwtFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
class DisplayControllerRejectionReasonTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private SpringDataDisplayJpaRepository displayRepository;
  @Autowired private SpringDataDisplayScreeningJpaRepository screeningRepository;
  @Autowired private JwtFactory jwtFactory;

  @ParameterizedTest
  @ValueSource(longs = {1L, 2L})
  void acceptedMemberAndLeaderCanGetRejectionReason(Long userId) throws Exception {
    Display display = rejectedDisplay();

    mockMvc
        .perform(
            get("/api/v1/display/{displayId}/rejection-reason", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(userId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.publishStatus").value("REJECTED"))
        .andExpect(jsonPath("$.success.data.rejectionReason").value("일정과 장소를 보완해주세요."));
  }

  @Test
  void rejectionReasonRequiresAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/v1/display/1/rejection-reason"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  private Display rejectedDisplay() {
    Display display =
        Display.create(
            new UserId(1L),
            "반려된 전시",
            "https://cdn.displayu.com/posters/main.png",
            "subtitle",
            "content",
            new DisplayLocation("디유 갤러리", new BigDecimal("37.5513"), new BigDecimal("126.9248")),
            "",
            "",
            "디유대학교",
            "디자인학부",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            new DisplayPeriod(
                LocalDate.of(2026, 9, 21),
                LocalDate.of(2026, 9, 28),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.ON_EXHIBITION);
    display.addTeamMember(
        new TeamMember(null, new UserId(1L), "팀장", TeamMemberRole.TEAM_LEADER, true));
    display.addTeamMember(
        new TeamMember(null, new UserId(2L), "참여자", TeamMemberRole.TEAM_MEM, true));
    display.requestReview();
    display = displayRepository.saveAndFlush(display);

    LocalDateTime requestedAt = LocalDateTime.of(2026, 9, 20, 9, 0);
    DisplayScreening screening = DisplayScreening.request(display, 1L, requestedAt);
    display.rejectReview();
    screening.reject(99L, "일정과 장소를 보완해주세요.", requestedAt.plusHours(1));
    displayRepository.saveAndFlush(display);
    screeningRepository.saveAndFlush(screening);
    return display;
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }
}
