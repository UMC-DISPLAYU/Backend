package com.example.demo.domain.display.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayInvitationStatus;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import com.example.demo.domain.display.domain.vo.DisplayLocation;
import com.example.demo.domain.display.domain.vo.DisplayPeriod;
import com.example.demo.domain.display.domain.vo.UserId;
import com.example.demo.domain.display.infrastructure.DisplayInvitationTokenHasher;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayInvitationJpaRepository;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayInvitationControllerTest {

  private static final String INVITATION_BASE_URL =
      "https://www.displayu.co.kr/display/invitation/";

  @Autowired private MockMvc mockMvc;

  @Autowired private JwtFactory jwtFactory;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private SpringDataDisplayInvitationJpaRepository invitationJpaRepository;

  @Autowired private DisplayInvitationTokenHasher tokenHasher;

  @BeforeEach
  void setUpPendingInvitationUniqueConstraint() {
    jdbcTemplate.execute(
        """
        ALTER TABLE display_invitation
        ADD COLUMN IF NOT EXISTS active_pending_invitee_user_id BIGINT
          GENERATED ALWAYS AS (
            CASE
              WHEN status = 'PENDING' AND deleted_at IS NULL THEN user_id2
              ELSE NULL
            END
          )
        """);
    jdbcTemplate.execute(
        """
        CREATE UNIQUE INDEX IF NOT EXISTS UQ_DISPLAYINVITATION_PENDING_DISPLAY_INVITEE
        ON display_invitation(display_id, active_pending_invitee_user_id)
        """);
  }

  @Test
  void issueInvitationCreatesFirstInvitationLink() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/displays/{displayId}/invitation", display.getId())
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
        .perform(get("/api/v1/displays/invitation/{token}", rawToken(firstUrl)))
        .perform(
            get("/api/v1/display/invitation/{token}", rawToken(firstUrl))
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("INVALID_DISPLAY_INVITATION_TOKEN"));

    mockMvc
        .perform(get("/api/v1/displays/invitation/{token}", rawToken(secondUrl)))
        .perform(
            get("/api/v1/display/invitation/{token}", rawToken(secondUrl))
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()));
  }

  @Test
  void issueInvitationFailsWhenRequesterIsNotOwnerOrTeamLeader() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    mockMvc
        .perform(
            post("/api/v1/displays/{displayId}/invitation", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_INVITATION_PERMISSION_DENIED"));

    Display savedDisplay = displayJpaRepository.findById(display.getId()).orElseThrow();
    assertThat(savedDisplay.getInvitationToken()).isNull();
  }

  @Test
  void issueInvitationReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    mockMvc
        .perform(post("/api/v1/displays/{displayId}/invitation", display.getId()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void disableInvitationIsIdempotent() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    String invitationUrl = invitationUrl(issue(display.getId()));

    mockMvc
        .perform(
            patch("/api/v1/displays/{displayId}/invitation", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(disableInvitationBody()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.invitationDisabledAt").value("2026-07-17T14:30:00Z"));

    mockMvc
        .perform(
            patch("/api/v1/displays/{displayId}/invitation", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(disableInvitationBody()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.invitationDisabledAt").value("2026-07-17T14:30:00Z"));

    mockMvc
        .perform(get("/api/v1/displays/invitation/{token}", rawToken(invitationUrl)))
        .perform(
            get("/api/v1/display/invitation/{token}", rawToken(invitationUrl))
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_INVITATION_DISABLED"));

    assertThat(pendingInvitations(display.getId(), 2L)).isEmpty();
  }

  @Test
  void disableInvitationFailsWhenRequesterIsNotOwnerOrTeamLeader() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    issue(display.getId());

    mockMvc
        .perform(
            patch("/api/v1/displays/{displayId}/invitation", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(2L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(disableInvitationBody()))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_INVITATION_PERMISSION_DENIED"));

    Display savedDisplay = displayJpaRepository.findById(display.getId()).orElseThrow();
    assertThat(savedDisplay.getInvitationDisabledAt()).isNull();
  }

  @Test
  void disableInvitationReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    issue(display.getId());

    mockMvc
        .perform(
            patch("/api/v1/displays/{displayId}/invitation", display.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(disableInvitationBody()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void getDisplayByInvitationReturnsNotFoundWhenTokenDoesNotExist() throws Exception {
    mockMvc
        .perform(get("/api/v1/displays/invitation/{token}", "not-existing-token"))
        .perform(
            get("/api/v1/display/invitation/{token}", "not-existing-token")
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("INVALID_DISPLAY_INVITATION_TOKEN"));

    assertThat(pendingInvitations(1L, 2L)).isEmpty();
  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void getDisplayByInvitationCreatesPendingInvitationForAuthenticatedRequester() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    try {
      String invitationUrl = invitationUrl(issue(display.getId()));

      mockMvc
          .perform(
              get("/api/v1/display/invitation/{token}", rawToken(invitationUrl))
                  .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success.data.displayId").value(display.getId()));

      List<DisplayInvitation> invitations = pendingInvitations(display.getId(), 2L);
      assertThat(invitations).hasSize(1);
      assertThat(invitations.get(0).getStatus()).isEqualTo(DisplayInvitationStatus.PENDING);
      assertThat(invitations.get(0).getInviterUserId().value()).isEqualTo(1L);
      assertThat(invitations.get(0).getInviteeUserId().value()).isEqualTo(2L);
    } finally {
      invitationJpaRepository.deleteAll();
      displayJpaRepository.deleteById(display.getId());
    }
  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void getDisplayByInvitationIsIdempotentForSameRequester() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    try {
      String invitationUrl = invitationUrl(issue(display.getId()));

      mockMvc
          .perform(
              get("/api/v1/display/invitation/{token}", rawToken(invitationUrl))
                  .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
          .andExpect(status().isOk());
      mockMvc
          .perform(
              get("/api/v1/display/invitation/{token}", rawToken(invitationUrl))
                  .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
          .andExpect(status().isOk());

      assertThat(pendingInvitations(display.getId(), 2L)).hasSize(1);
    } finally {
      invitationJpaRepository.deleteAll();
      displayJpaRepository.deleteById(display.getId());
    }
  }

  @Test
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  void getDisplayByInvitationCreatesOnlyOnePendingInvitationForConcurrentRequests()
      throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    String token = rawToken(invitationUrl(issue(display.getId())));
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    CountDownLatch startLatch = new CountDownLatch(1);

    try {
      List<Future<Integer>> responses =
          List.of(
              executorService.submit(
                  () -> getDisplayByInvitationStatusAfterStart(startLatch, token)),
              executorService.submit(
                  () -> getDisplayByInvitationStatusAfterStart(startLatch, token)));

      startLatch.countDown();

      for (Future<Integer> response : responses) {
        assertThat(response.get(5, TimeUnit.SECONDS)).isEqualTo(200);
      }
      assertThat(pendingInvitations(display.getId(), 2L)).hasSize(1);
    } finally {
      executorService.shutdownNow();
      invitationJpaRepository.deleteAll();
      displayJpaRepository.deleteById(display.getId());
    }
  }

  @Test
  void getDisplayByInvitationDoesNotCreateInvitationForAcceptedTeamMember() throws Exception {
    Display display = display();
    display.addTeamMember(
        new TeamMember(null, new UserId(2L), "팀원", TeamMemberRole.TEAM_MEM, true));
    display = displayJpaRepository.saveAndFlush(display);
    String invitationUrl = invitationUrl(issue(display.getId()));

    mockMvc
        .perform(
            get("/api/v1/display/invitation/{token}", rawToken(invitationUrl))
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()));

    assertThat(pendingInvitations(display.getId(), 2L)).isEmpty();
  }

  @Test
  void getDisplayByInvitationReturnsUnauthorizedWithoutAuthentication() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());
    String invitationUrl = invitationUrl(issue(display.getId()));

    mockMvc
        .perform(get("/api/v1/display/invitation/{token}", rawToken(invitationUrl)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));

    assertThat(pendingInvitations(display.getId(), 2L)).isEmpty();
  }

  @Test
  void disableInvitationFailsWhenInvitationIsNotIssued() throws Exception {
    Display display = displayJpaRepository.saveAndFlush(display());

    mockMvc
        .perform(
            patch("/api/v1/displays/{displayId}/invitation", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(disableInvitationBody()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_INVITATION_NOT_ISSUED"));
  }

  private MvcResult issue(Long displayId) throws Exception {
    return mockMvc
        .perform(
            post("/api/v1/displays/{displayId}/invitation", displayId)
                .header(HttpHeaders.AUTHORIZATION, bearer(1L)))
        .andExpect(status().isOk())
        .andReturn();
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }

  private static String disableInvitationBody() {
    return """
        {
          "invitationEnabled": false
        }
        """;
  }

  private String invitationUrl(MvcResult result) throws Exception {
    return JsonPath.read(result.getResponse().getContentAsString(), "$.success.data.invitationUrl");
  }

  private static String rawToken(String invitationUrl) {
    return invitationUrl.substring(INVITATION_BASE_URL.length());
  }

  private int getDisplayByInvitationStatusAfterStart(CountDownLatch startLatch, String token)
      throws Exception {
    startLatch.await(5, TimeUnit.SECONDS);
    return mockMvc
        .perform(
            get("/api/v1/display/invitation/{token}", token)
                .header(HttpHeaders.AUTHORIZATION, bearer(2L)))
        .andReturn()
        .getResponse()
        .getStatus();
  }

  private List<DisplayInvitation> pendingInvitations(Long displayId, Long inviteeUserId) {
    return invitationJpaRepository.findByDisplayIdAndInviteeUserIdValueAndStatusAndDeletedAtIsNull(
        displayId, inviteeUserId, DisplayInvitationStatus.PENDING);
  }

  private static Display display() {
    Display display =
        Display.create(
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
    display.addTeamMember(
        new TeamMember(null, new UserId(1L), "팀장", TeamMemberRole.TEAM_LEADER, true));
    return display;
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
