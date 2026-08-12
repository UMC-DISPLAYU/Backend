package com.example.demo.domain.display.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayControllerStatusTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private JwtFactory jwtFactory;

  @Test
  void hideDisplayChangesPublishedDisplayToDraftWhenRequesterIsTeamLeader() throws Exception {
    Display display = displayWithTeamMembers();
    display.publish();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/draft", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.status").value("DRAFT"))
        .andExpect(
            jsonPath("$.meta.path").value("/api/v1/display/%d/draft".formatted(display.getId())));
  }

  @Test
  void hideDisplayReturnsSuccessWhenDisplayIsAlreadyDraft() throws Exception {
    Display display = displayWithTeamMembers();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/draft", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.status").value("DRAFT"));
  }

  @Test
  void hideDisplayReturnsForbiddenWhenRequesterIsNotTeamLeader() throws Exception {
    Display display = displayWithTeamMembers();
    display.publish();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/draft", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
  }

  @Test
  void hideDisplayReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayWithTeamMembers();
    display.publish();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(patch("/api/v1/display/{displayId}/draft", display.getId()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void hideDisplayReturnsNotFoundWhenDisplayDoesNotExist() throws Exception {
    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/draft", 999_999L)
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
  }

  @Test
  void hideDisplayReturnsBadRequestWhenDisplayIdIsInvalid() throws Exception {
    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/draft", -1L)
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
  }

  @Test
  void hideDisplayReturnsMethodNotAllowedWhenDisplayIdIsMissing() throws Exception {
    mockMvc
        .perform(patch("/api/v1/display/draft").header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("METHOD_NOT_ALLOWED"));
  }

  @Test
  void deleteDisplayMarksDisplayDeletedWhenRequesterIsTeamLeader() throws Exception {
    Display display = displayWithTeamMembers();
    display.publish();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            delete("/api/v1/display/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data").doesNotExist());

    displayJpaRepository.flush();
    Display deletedDisplay = displayJpaRepository.findById(display.getId()).orElseThrow();
    org.assertj.core.api.Assertions.assertThat(deletedDisplay.isDeleted()).isTrue();
  }

  @Test
  void deleteDisplayReturnsForbiddenWhenRequesterIsNotTeamLeader() throws Exception {
    Display display = displayWithTeamMembers();
    display.publish();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            delete("/api/v1/display/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
  }

  @Test
  void deleteDisplayReturnsDisplayNotFoundWhenAlreadyDeleted() throws Exception {
    Display display = displayWithTeamMembers();
    display.delete();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            delete("/api/v1/display/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("DISPLAY_NOT_FOUND"));
  }

  @Test
  void deletedDisplayIsNotReturnedByDetailEvenForTeamLeader() throws Exception {
    Display display = displayWithTeamMembers();
    display.publish();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            delete("/api/v1/display/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            get("/api/v1/display/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
  }

  @Test
  void hiddenDisplayIsNotReturnedBySearch() throws Exception {
    Display display = displayWithTeamMembers();
    display.publish();
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/draft", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            get("/api/v1/display/search")
                .param("searchWord", "FORM")
                .param("cursor", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.exhibitions", hasSize(0)));
  }

  private static Display displayWithTeamMembers() {
    Display display =
        Display.create(
            new UserId(1L),
            "FORM 2026",
            "https://cdn.displayu.com/posters/main.png",
            "전시 부제",
            "전시 설명",
            new DisplayLocation(
                "중앙대학교 전시장", BigDecimal.valueOf(37.5513), BigDecimal.valueOf(126.9248)),
            "",
            "전시 유의사항",
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
