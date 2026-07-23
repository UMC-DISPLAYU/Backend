package com.example.demo.domain.display.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.DisplayInvitationTokenHasher;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.global.security.JwtFactory;
import com.jayway.jsonpath.JsonPath;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayInvitationControllerTest {

  private static final String INVITATION_BASE_URL = "https://displayu.co.kr/display/invitation/";

  @Autowired private MockMvc mockMvc;

  @Autowired private JwtFactory jwtFactory;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private DisplayInvitationTokenHasher tokenHasher;

  @Test
  void issueInvitationCreatesFirstInvitationLink() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/display/{displayId}/invitation", display.getId())
                    .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resultType").value("SUCCESS"))
            .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
            .andExpect(jsonPath("$.success.data.invitationUrl").isString())
            .andReturn();

    String invitationUrl = invitationUrl(result);
    String rawToken = rawToken(invitationUrl);
    Display savedDisplay = displayJpaRepository.findById(display.getId()).orElseThrow();

    assertThat(invitationUrl).startsWith(INVITATION_BASE_URL);
    assertThat(rawToken).doesNotContain("+", "/", "=");
    assertThat(savedDisplay.getInvitationToken()).isEqualTo(tokenHasher.hash(rawToken));
    assertThat(savedDisplay.getInvitationToken()).isNotEqualTo(rawToken);
    assertThat(savedDisplay.getInvitationDisabledAt()).isNull();
  }

  @Test
  void reissueInvitationChangesTokenAndOldTokenCannotBeUsed() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    String firstUrl = invitationUrl(issue(display.getId()));
    String secondUrl = invitationUrl(issue(display.getId()));

    assertThat(firstUrl).isNotEqualTo(secondUrl);

    mockMvc
        .perform(get("/api/v1/display/invitation/{token}", rawToken(firstUrl)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("INVALID_DISPLAY_INVITATION_TOKEN"));

    mockMvc
        .perform(get("/api/v1/display/invitation/{token}", rawToken(secondUrl)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()));
  }

  @Test
  void issueInvitationFailsWhenRequesterIsNotOwnerOrTeamLeader() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/invitation", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_INVITATION_PERMISSION_DENIED"));

    Display savedDisplay = displayJpaRepository.findById(display.getId()).orElseThrow();
    assertThat(savedDisplay.getInvitationToken()).isNull();
  }

  @Test
  void disableInvitationIsIdempotent() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    String invitationUrl = invitationUrl(issue(display.getId()));

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/invitation/disable", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.invitationDisabledAt").value("2026-07-17T23:30:00"));

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/invitation/disable", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.invitationDisabledAt").value("2026-07-17T23:30:00"));

    mockMvc
        .perform(get("/api/v1/display/invitation/{token}", rawToken(invitationUrl)))
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_INVITATION_DISABLED"));
  }

  @Test
  void disableInvitationFailsWhenRequesterIsNotOwnerOrTeamLeader() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    issue(display.getId());

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/invitation/disable", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_INVITATION_PERMISSION_DENIED"));

    Display savedDisplay = displayJpaRepository.findById(display.getId()).orElseThrow();
    assertThat(savedDisplay.getInvitationDisabledAt()).isNull();
  }

  @Test
  void getDisplayByInvitationReturnsNotFoundWhenTokenDoesNotExist() throws Exception {
    mockMvc
        .perform(get("/api/v1/display/invitation/{token}", "not-existing-token"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("INVALID_DISPLAY_INVITATION_TOKEN"));
  }

  @Test
  void disableInvitationFailsWhenInvitationIsNotIssued() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/invitation/disable", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_INVITATION_NOT_ISSUED"));
  }

  private MvcResult issue(Long displayId) throws Exception {
    return mockMvc
        .perform(
            post("/api/v1/display/{displayId}/invitation", displayId)
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andReturn();
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }

  private String invitationUrl(MvcResult result) throws Exception {
    return JsonPath.read(result.getResponse().getContentAsString(), "$.success.data.invitationUrl");
  }

  private static String rawToken(String invitationUrl) {
    return invitationUrl.substring(INVITATION_BASE_URL.length());
  }

  private static Display display() {
    return Display.create(
        new UserId(1L),
        "FORM 2026",
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
        DisplayRegion.SEOUL,
        new DisplayPeriod(
            LocalDate.of(2026, 5, 28),
            LocalDate.of(2026, 6, 5),
            LocalTime.of(10, 0),
            LocalTime.of(18, 0)),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }

  private static BigDecimal bd(String value) {
    return new BigDecimal(value);
  }

  @TestConfiguration
  static class FixedClockConfig {

    @Bean
    @Primary
    Clock fixedClock() {
      return Clock.fixed(Instant.parse("2026-07-17T14:30:00Z"), ZoneId.of("Asia/Seoul"));
    }
  }
}
