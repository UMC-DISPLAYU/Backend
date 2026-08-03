package com.example.demo.domain.display.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.global.security.JwtFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayControllerReservationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private JwtFactory jwtFactory;

  @Test
  void updateDisplayReservationUpdatesOpenPoliciesWhenRequesterIsTeamLeader() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/reservation", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationRequest()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.artworkContentOpen").value("ON_EXHIBITION"))
        .andExpect(jsonPath("$.success.data.exhibitionContentOpen").value("IMMEDIATELY"))
        .andExpect(
            jsonPath("$.meta.path")
                .value("/api/v1/display/%d/reservation".formatted(display.getId())));
  }

  @Test
  void updateDisplayReservationReturnsForbiddenWhenRequesterIsNotTeamLeader() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/reservation", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationRequest()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
  }

  @Test
  void updateDisplayReservationReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/reservation", display.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationRequest()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void updateDisplayReservationReturnsBadRequestWhenOpenPolicyIsMissing() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/reservation", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "artworkContentOpen": "ON_EXHIBITION"
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
  }

  private static String reservationRequest() {
    return """
        {
          "artworkContentOpen": "ON_EXHIBITION",
          "exhibitionContentOpen": "IMMEDIATELY"
        }
        """;
  }

  private static Display displayWithTeamMembers() {
    Display display =
        Display.create(
            new UserId(1L),
            "FORM 2026",
            "https://cdn.displayu.com/posters/main.png",
            "기존 부제",
            "기존 설명",
            new DisplayLocation(
                "기존 전시장", BigDecimal.valueOf(37.5513), BigDecimal.valueOf(126.9248)),
            "",
            "기존 유의사항",
            "중앙대학교",
            "디자인학부",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 5, 28),
                LocalDate.of(2026, 6, 5),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.ON_EXHIBITION);
    display.addTeamMember(
        new TeamMember(null, new UserId(1L), "팀장", TeamMemberRole.TEAM_LEADER, true));
    display.addTeamMember(
        new TeamMember(null, new UserId(2L), "팀원", TeamMemberRole.TEAM_MEM, true));
    return display;
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }
}
