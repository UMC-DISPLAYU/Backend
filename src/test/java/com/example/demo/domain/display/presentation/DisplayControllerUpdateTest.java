package com.example.demo.domain.display.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class DisplayControllerUpdateTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private JwtFactory jwtFactory;

  @Test
  void getDraftDisplayDetailSucceedsWhenRequesterIsAcceptedTeamMember() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            get("/api/v1/displays/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.status").value("DRAFT"));
  }

  @Test
  void updateDisplayUpdatesOptionalFieldsWhenRequesterIsTeamLeader() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/displays")
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest(display.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.title").value("FORM 2026 (수정본)"))
        .andExpect(jsonPath("$.success.data.subtitle").value("변경된 전시 부제목입니다."))
        .andExpect(jsonPath("$.success.data.content").value("변경된 전시 소개글입니다."))
        .andExpect(jsonPath("$.success.data.note").value("물품 보관소를 운영하지 않습니다."))
        .andExpect(jsonPath("$.success.data.location.placeName").value("중앙대학교 301관 갤러리 3층 전시장"))
        .andExpect(jsonPath("$.success.data.displayType").value("GRADUATION"))
        .andExpect(jsonPath("$.success.data.displayFields[0]").value("DESIGN"))
        .andExpect(jsonPath("$.success.data.displayFields[1]").value("VIDEO"))
        .andExpect(jsonPath("$.success.data.period.startDate").value("2026-05-29"))
        .andExpect(jsonPath("$.success.data.period.endDate").value("2026-06-06"))
        .andExpect(jsonPath("$.success.data.period.startTime").value("09:00:00"))
        .andExpect(jsonPath("$.success.data.period.endTime").value("19:00:00"))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/displays"));
  }

  @Test
  void updateDisplayReturnsForbiddenWhenRequesterIsNotTeamLeader() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/displays")
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest(display.getId())))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
  }

  @Test
  void updateDisplayReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/displays")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest(display.getId())))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  private static String updateRequest(Long displayId) {
    return """
        {
          "displayId": %d,
          "title": "FORM 2026 (수정본)",
          "posterImageUrl": "https://cdn.displayu.com/posters/updated.png",
          "type": "GRADUATION",
          "fields": ["DESIGN", "MEDIA"],
          "schoolOrOrganization": "중앙대학교",
          "departmentOrClub": "디자인학부 시각디자인",
          "hostOrganizationName": null,
          "subtitle": "변경된 전시 부제목입니다.",
          "description": "변경된 전시 소개글입니다.",
          "startDate": "2026-05-29",
          "endDate": "2026-06-06",
          "openTime": "09:00",
          "closeTime": "19:00",
          "placeName": "중앙대학교 301관 갤러리 3층 전시장",
          "precautions": "물품 보관소를 운영하지 않습니다."
        }
        """
        .formatted(displayId);
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
