package com.example.demo.domain.display.presentation;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayInvitationJpaRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataTeamMemberJpaRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DisplayMemberInvitationControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private JwtFactory jwtFactory;

  @Autowired private UserJpaRepository userJpaRepository;

  @Autowired private SpringDataDisplayJpaRepository displayJpaRepository;

  @Autowired private SpringDataDisplayInvitationJpaRepository invitationJpaRepository;

  @Autowired private SpringDataTeamMemberJpaRepository teamMemberJpaRepository;

  @Test
  void inviteCreatesPendingDisplayMemberInvitation() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User invitee = userJpaRepository.save(user("invitee"));
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/invitations", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(leader.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(inviteRequest(invitee.getId())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.inviterUserId").value(leader.getId()))
        .andExpect(jsonPath("$.success.data.inviteeUserId").value(invitee.getId()))
        .andExpect(jsonPath("$.success.data.status").value("PENDING"));
  }

  @Test
  void inviteReturnsConflictWhenPendingInvitationAlreadyExists() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User invitee = userJpaRepository.save(user("invitee"));
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));
    invite(display.getId(), leader.getId(), invitee.getId());

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/invitations", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(leader.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(inviteRequest(invitee.getId())))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("PENDING_DISPLAY_INVITATION_EXISTS"));
  }

  @Test
  void acceptUpdatesInvitationAndCreatesTeamMember() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User invitee = userJpaRepository.save(user("invitee"));
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));
    Long invitationId = invite(display.getId(), leader.getId(), invitee.getId());

    mockMvc
        .perform(
            post("/api/v1/display-invitations/{invitationId}/accept", invitationId)
                .header(HttpHeaders.AUTHORIZATION, bearer(invitee.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.invitationId").value(invitationId))
        .andExpect(jsonPath("$.success.data.status").value("ACCEPTED"))
        .andExpect(jsonPath("$.success.data.respondedAt").value("2026-07-22T18:00:00"));

    DisplayInvitation invitation = invitationJpaRepository.findById(invitationId).orElseThrow();
    assertThat(invitation.getStatus()).isEqualTo(DisplayInvitationStatus.ACCEPTED);
    assertThat(
            teamMemberJpaRepository.existsByDisplayIdAndUserIdValueAndAcceptedTrue(
                display.getId(), invitee.getId()))
        .isTrue();
  }

  @Test
  void acceptReturnsForbiddenWhenRequesterIsNotInvitee() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User invitee = userJpaRepository.save(user("invitee"));
    User other = userJpaRepository.save(user("other"));
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));
    Long invitationId = invite(display.getId(), leader.getId(), invitee.getId());

    mockMvc
        .perform(
            post("/api/v1/display-invitations/{invitationId}/accept", invitationId)
                .header(HttpHeaders.AUTHORIZATION, bearer(other.getId())))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_INVITATION_INVITEE_MISMATCH"));
  }

  @Test
  void rejectUpdatesInvitationWithoutCreatingTeamMember() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User invitee = userJpaRepository.save(user("invitee"));
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));
    Long invitationId = invite(display.getId(), leader.getId(), invitee.getId());

    mockMvc
        .perform(
            post("/api/v1/display-invitations/{invitationId}/reject", invitationId)
                .header(HttpHeaders.AUTHORIZATION, bearer(invitee.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.status").value("REJECTED"))
        .andExpect(jsonPath("$.success.data.respondedAt").value("2026-07-22T18:00:00"));

    DisplayInvitation invitation = invitationJpaRepository.findById(invitationId).orElseThrow();
    assertThat(invitation.getStatus()).isEqualTo(DisplayInvitationStatus.REJECTED);
    assertThat(
            teamMemberJpaRepository.existsByDisplayIdAndUserIdValueAndAcceptedTrue(
                display.getId(), invitee.getId()))
        .isFalse();
  }

  private Long invite(Long displayId, Long leaderUserId, Long inviteeUserId) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/display/{displayId}/invitations", displayId)
                    .header(HttpHeaders.AUTHORIZATION, bearer(leaderUserId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(inviteRequest(inviteeUserId)))
            .andExpect(status().isCreated())
            .andReturn();
    Number invitationId =
        JsonPath.read(result.getResponse().getContentAsString(), "$.success.data.invitationId");
    return invitationId.longValue();
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }

  private static String inviteRequest(Long inviteeUserId) {
    return """
        {
          "inviteeUserId": %d,
          "role": "TEAM_MEM"
        }
        """
        .formatted(inviteeUserId);
  }

  private static User user(String nickname) {
    return User.builder()
        .provider(Provider.Google)
        .providerId("provider-" + nickname)
        .name(nickname)
        .nickname(nickname)
        .socialEmail(nickname + "@displayu.com")
        .build();
  }

  private static Display displayWithLeader(User leader) {
    Display display =
        Display.create(
            new UserId(leader.getId()),
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
        new TeamMember(
            null,
            new UserId(leader.getId()),
            leader.getNickname(),
            TeamMemberRole.TEAM_LEADER,
            true));
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
      return Clock.fixed(Instant.parse("2026-07-22T09:00:00Z"), ZoneId.of("Asia/Seoul"));
    }
  }
}
