package com.example.demo.domain.display.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayInvitationJpaRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataDisplayJpaRepository;
import com.example.demo.domain.display.infrastructure.persistence.SpringDataTeamMemberJpaRepository;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.type.Provider;
import com.example.demo.domain.user.infrastructure.persistence.UserJpaRepository;
import com.example.demo.global.security.JwtFactory;
import com.jayway.jsonpath.JsonPath;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            post("/api/v1/display-invitations/displays/{displayId}", display.getId())
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
  void inviteUsesTeamMemberRoleWhenRequestRoleIsMissing() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User invitee = userJpaRepository.save(user("invitee"));
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));

    mockMvc
        .perform(
            post("/api/v1/display-invitations/displays/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(leader.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(inviteRequestWithoutRole(invitee.getId())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
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
            post("/api/v1/display-invitations/displays/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(leader.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(inviteRequest(invitee.getId())))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("PENDING_DISPLAY_INVITATION_EXISTS"));
  }

  @Test
  void inviteReturnsConflictWhenInviteeIsWithdrawnUser() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User invitee = user("invitee");
    invitee.withdraw(LocalDateTime.of(2026, 7, 23, 12, 0));
    userJpaRepository.save(invitee);
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));

    mockMvc
        .perform(
            post("/api/v1/display-invitations/displays/{displayId}", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(leader.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(inviteRequest(invitee.getId())))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("ALREADY_WITHDRAWN_USER"));
  }

  @Test
  void inviteReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/display-invitations/displays/{displayId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(inviteRequest(1L)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
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
                .header(HttpHeaders.AUTHORIZATION, bearer(invitee.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(acceptRequest("전시용 닉네임")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.invitationId").value(invitationId))
        .andExpect(jsonPath("$.success.data.status").value("ACCEPTED"))
        .andExpect(jsonPath("$.success.data.respondedAt").value("2026-07-22T09:00:00Z"));

    DisplayInvitation invitation = invitationJpaRepository.findById(invitationId).orElseThrow();
    assertThat(invitation.getStatus()).isEqualTo(DisplayInvitationStatus.ACCEPTED);
    assertThat(
            teamMemberJpaRepository
                .existsByDisplayIdAndUserIdValueAndAcceptedTrueAndDeletedAtIsNull(
                    display.getId(), invitee.getId()))
        .isTrue();
    Display savedDisplay = displayJpaRepository.findById(display.getId()).orElseThrow();
    assertThat(savedDisplay.getTeamMembers())
        .anySatisfy(
            teamMember -> {
              assertThat(teamMember.getUserId().value()).isEqualTo(invitee.getId());
              assertThat(teamMember.getDisplayNickname()).isEqualTo("전시용 닉네임");
            });
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
                .header(HttpHeaders.AUTHORIZATION, bearer(other.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(acceptRequest("다른 사용자")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_INVITATION_INVITEE_MISMATCH"));
  }

  @Test
  void acceptReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/display-invitations/{invitationId}/accept", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(acceptRequest("전시용 닉네임")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
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
        .andExpect(jsonPath("$.success.data.respondedAt").value("2026-07-22T09:00:00Z"));

    DisplayInvitation invitation = invitationJpaRepository.findById(invitationId).orElseThrow();
    assertThat(invitation.getStatus()).isEqualTo(DisplayInvitationStatus.REJECTED);
    assertThat(
            teamMemberJpaRepository
                .existsByDisplayIdAndUserIdValueAndAcceptedTrueAndDeletedAtIsNull(
                    display.getId(), invitee.getId()))
        .isFalse();
  }

  @Test
  void rejectReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(post("/api/v1/display-invitations/{invitationId}/reject", 1L))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void getMembersReturnsNonDeletedDisplayMembersWithUserState() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User member = user("member");
    member.verifySchoolEmail("member@school.ac.kr", "중앙대학교");
    member.completeArtistVerification();
    userJpaRepository.save(member);
    User pendingInvitee = userJpaRepository.save(user("pending"));
    User withdrawn = user("withdrawn");
    withdrawn.withdraw(LocalDateTime.of(2026, 7, 23, 12, 0));
    userJpaRepository.save(withdrawn);
    User exited = userJpaRepository.save(user("exited"));
    Display display = displayWithLeader(leader);
    display.addTeamMember(
        new TeamMember(
            null, new UserId(member.getId()), member.getNickname(), TeamMemberRole.TEAM_MEM, true));
    display.addTeamMember(
        new TeamMember(
            null,
            new UserId(withdrawn.getId()),
            withdrawn.getNickname(),
            TeamMemberRole.TEAM_MEM,
            true));
    TeamMember exitedMember =
        new TeamMember(
            null, new UserId(exited.getId()), exited.getNickname(), TeamMemberRole.TEAM_MEM, true);
    exitedMember.softDelete(LocalDateTime.of(2026, 7, 24, 12, 0));
    display.addTeamMember(exitedMember);
    displayJpaRepository.saveAndFlush(display);
    invite(display.getId(), leader.getId(), pendingInvitee.getId());

    mockMvc
        .perform(
            get("/api/v1/display/{displayId}/members", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(member.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.memberAccept.length()").value(3))
        .andExpect(jsonPath("$.success.data.memberAccept[0].userId").value(leader.getId()))
        .andExpect(jsonPath("$.success.data.memberAccept[0].role").value("TEAM_LEADER"))
        .andExpect(jsonPath("$.success.data.memberAccept[1].userId").value(member.getId()))
        .andExpect(jsonPath("$.success.data.memberAccept[1].loggedIn").value(true))
        .andExpect(jsonPath("$.success.data.memberAccept[1].artistVerified").value(true))
        .andExpect(jsonPath("$.success.data.memberAccept[1].accepted").value(true))
        .andExpect(jsonPath("$.success.data.memberAccept[1].role").value("TEAM_MEM"))
        .andExpect(jsonPath("$.success.data.memberAccept[2].userId").value(withdrawn.getId()))
        .andExpect(jsonPath("$.success.data.memberAccept[2].loggedIn").value(false))
        .andExpect(jsonPath("$.success.data.memberAccept[2].artistVerified").value(false))
        .andExpect(jsonPath("$.success.data.memberPending.length()").value(1))
        .andExpect(jsonPath("$.success.data.memberPending[0].teamMemberId").value(nullValue()))
        .andExpect(jsonPath("$.success.data.memberPending[0].userId").value(pendingInvitee.getId()))
        .andExpect(jsonPath("$.success.data.memberPending[0].accepted").value(false))
        .andExpect(jsonPath("$.success.data.memberPending[0].role").value("TEAM_MEM"));
  }

  @Test
  void getMembersReturnsAcceptedDisplayMembersWhenRequesterIsTeamLeader() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User member = userJpaRepository.save(user("member"));
    Display display = displayWithLeader(leader);
    display.addTeamMember(
        new TeamMember(
            null, new UserId(member.getId()), member.getNickname(), TeamMemberRole.TEAM_MEM, true));
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            get("/api/v1/display/{displayId}/members", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(leader.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.memberAccept.length()").value(2))
        .andExpect(jsonPath("$.success.data.memberPending.length()").value(0));
  }

  @Test
  void getMembersReturnsDisplayMembersWithoutAuthentication() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));

    mockMvc
        .perform(get("/api/v1/display/{displayId}/members", display.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.memberAccept.length()").value(1))
        .andExpect(jsonPath("$.success.data.memberPending.length()").value(0));
  }

  @Test
  void exitDisplaySoftDeletesAcceptedNonLeaderTeamMember() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User member = userJpaRepository.save(user("member"));
    Display display = displayWithLeader(leader);
    display.addTeamMember(
        new TeamMember(
            null, new UserId(member.getId()), member.getNickname(), TeamMemberRole.TEAM_MEM, true));
    displayJpaRepository.saveAndFlush(display);

    mockMvc
        .perform(
            delete("/api/v1/display/{displayId}/exit", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(member.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data").doesNotExist());

    TeamMember exitedMember =
        teamMemberJpaRepository.findAll().stream()
            .filter(teamMember -> teamMember.getDisplay().getId().equals(display.getId()))
            .filter(teamMember -> teamMember.getUserId().value().equals(member.getId()))
            .findFirst()
            .orElseThrow();
    assertThat(exitedMember.getDeletedAt()).isEqualTo(LocalDateTime.of(2026, 7, 22, 9, 0));
    assertThat(
            teamMemberJpaRepository
                .existsByDisplayIdAndUserIdValueAndAcceptedTrueAndDeletedAtIsNull(
                    display.getId(), member.getId()))
        .isFalse();
  }

  @Test
  void exitDisplayReturnsForbiddenWhenRequesterIsTeamLeader() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));

    mockMvc
        .perform(
            delete("/api/v1/display/{displayId}/exit", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(leader.getId())))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
  }

  @Test
  void exitDisplayReturnsDisplayMemberNotFoundWhenRequesterIsNotMember() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User other = userJpaRepository.save(user("other"));
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));

    mockMvc
        .perform(
            delete("/api/v1/display/{displayId}/exit", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(other.getId())))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_MEMBER_NOT_FOUND"));
  }

  @Test
  void exitDisplayReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(delete("/api/v1/display/{displayId}/exit", 1L))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void updateMyDisplayNicknameChangesAcceptedTeamMemberNickname() throws Exception {
    User leader = user("leader");
    leader.verifySchoolEmail("leader@school.ac.kr", "중앙대학교");
    leader.completeArtistVerification();
    userJpaRepository.save(leader);
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));

    mockMvc
        .perform(
            patch("/api/v1/display/me/nickname")
                .header(HttpHeaders.AUTHORIZATION, bearer(leader.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateNicknameRequest(display.getId(), "새 전시 닉네임")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.userId").value(leader.getId()))
        .andExpect(jsonPath("$.success.data.displayNickname").value("새 전시 닉네임"))
        .andExpect(jsonPath("$.success.data.loggedIn").value(true))
        .andExpect(jsonPath("$.success.data.artistVerified").value(true))
        .andExpect(jsonPath("$.success.data.accepted").value(true))
        .andExpect(jsonPath("$.success.data.role").value("TEAM_LEADER"));

    assertThat(
            teamMemberJpaRepository
                .findByDisplayIdAndUserIdValueAndAcceptedTrueAndDeletedAtIsNull(
                    display.getId(), leader.getId())
                .orElseThrow()
                .getDisplayNickname())
        .isEqualTo("새 전시 닉네임");
  }

  @Test
  void updateMyDisplayNicknameReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(
            patch("/api/v1/display/me/nickname")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateNicknameRequest(1L, "새 전시 닉네임")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void getMyInvitationsReturnsPendingInvitationsReceivedByRequester() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User invitee = userJpaRepository.save(user("invitee"));
    User otherInvitee = userJpaRepository.save(user("otherInvitee"));
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));
    Long invitationId = invite(display.getId(), leader.getId(), invitee.getId());
    invite(display.getId(), leader.getId(), otherInvitee.getId());

    mockMvc
        .perform(
            get("/api/v1/display-invitations/me")
                .header(HttpHeaders.AUTHORIZATION, bearer(invitee.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.invitations.length()").value(1))
        .andExpect(jsonPath("$.success.data.invitations[0].invitationId").value(invitationId))
        .andExpect(jsonPath("$.success.data.invitations[0].displayId").value(display.getId()))
        .andExpect(
            jsonPath("$.success.data.invitations[0].thumbnailUrl")
                .value("https://cdn.displayu.com/posters/main.png"))
        .andExpect(jsonPath("$.success.data.invitations[0].startDate").value("2026-05-28"))
        .andExpect(jsonPath("$.success.data.invitations[0].endDate").value("2026-06-05"))
        .andExpect(jsonPath("$.success.data.invitations[0].location").value("SEOUL"))
        .andExpect(jsonPath("$.success.data.invitations[0].userNickname").value("leader"))
        .andExpect(jsonPath("$.success.data.invitations[0].leaderName").value("전시 리더"))
        .andExpect(jsonPath("$.success.data.invitations[0].title").value("FORM 2026"))
        .andExpect(
            jsonPath("$.success.data.invitations[0].schoolDepartmentName")
                .value("organization department"))
        .andExpect(jsonPath("$.success.data.invitations[0].placeName").value("전시장"));
  }

  @Test
  void getInvitationDisplaysReturnsPendingInvitedDisplaysByRequester() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User invitee = userJpaRepository.save(user("invitee"));
    User otherInvitee = userJpaRepository.save(user("otherInvitee"));
    Display pendingDisplay = displayJpaRepository.saveAndFlush(displayWithLeader(leader));
    invite(pendingDisplay.getId(), leader.getId(), invitee.getId());
    invite(pendingDisplay.getId(), leader.getId(), otherInvitee.getId());

    Display acceptedDisplay = displayJpaRepository.saveAndFlush(displayWithLeader(leader));
    Long acceptedInvitationId = invite(acceptedDisplay.getId(), leader.getId(), invitee.getId());
    mockMvc
        .perform(
            post("/api/v1/display-invitations/{invitationId}/accept", acceptedInvitationId)
                .header(HttpHeaders.AUTHORIZATION, bearer(invitee.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(acceptRequest("전시용 닉네임")))
        .andExpect(status().isOk());

    Display rejectedDisplay = displayJpaRepository.saveAndFlush(displayWithLeader(leader));
    Long rejectedInvitationId = invite(rejectedDisplay.getId(), leader.getId(), invitee.getId());
    mockMvc
        .perform(
            post("/api/v1/display-invitations/{invitationId}/reject", rejectedInvitationId)
                .header(HttpHeaders.AUTHORIZATION, bearer(invitee.getId())))
        .andExpect(status().isOk());

    Display deletedInvitationDisplay = displayWithLeader(leader);
    deletedInvitationDisplay.addInvitation(
        new DisplayInvitation(
            null,
            new UserId(leader.getId()),
            new UserId(invitee.getId()),
            DisplayInvitationStatus.PENDING,
            LocalDateTime.of(2026, 7, 21, 10, 0),
            null,
            LocalDateTime.of(2026, 7, 21, 11, 0)));
    displayJpaRepository.saveAndFlush(deletedInvitationDisplay);

    mockMvc
        .perform(
            get("/api/v1/display-invitations")
                .header(HttpHeaders.AUTHORIZATION, bearer(invitee.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.exhibitions.length()").value(1))
        .andExpect(
            jsonPath("$.success.data.exhibitions[0].displayId").value(pendingDisplay.getId()))
        .andExpect(jsonPath("$.success.data.exhibitions[0].title").value("FORM 2026"))
        .andExpect(
            jsonPath("$.success.data.exhibitions[0].posterImageUrl")
                .value("https://cdn.displayu.com/posters/main.png"))
        .andExpect(
            jsonPath("$.success.data.exhibitions[0].schoolDepartmentName")
                .value("organization department"))
        .andExpect(jsonPath("$.success.data.exhibitions[0].startedAt").value("2026-05-28"))
        .andExpect(jsonPath("$.success.data.exhibitions[0].endedAt").value("2026-06-05"))
        .andExpect(jsonPath("$.success.data.exhibitions[0].dayLeft").value(-47))
        .andExpect(jsonPath("$.success.data.exhibitions[0].isArchived").value(false));
  }

  @Test
  void getInvitationDisplaysReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/v1/display-invitations"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void getMyInvitationsDoesNotReturnAcceptedInvitation() throws Exception {
    User leader = userJpaRepository.save(user("leader"));
    User invitee = userJpaRepository.save(user("invitee"));
    Display display = displayJpaRepository.saveAndFlush(displayWithLeader(leader));
    Long invitationId = invite(display.getId(), leader.getId(), invitee.getId());

    mockMvc
        .perform(
            post("/api/v1/display-invitations/{invitationId}/accept", invitationId)
                .header(HttpHeaders.AUTHORIZATION, bearer(invitee.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(acceptRequest("전시용 닉네임")))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            get("/api/v1/display-invitations/me")
                .header(HttpHeaders.AUTHORIZATION, bearer(invitee.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.invitations.length()").value(0));
  }

  @Test
  void getMyInvitationsReturnsUnauthorizedWithoutAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/v1/display-invitations/me"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  private Long invite(Long displayId, Long leaderUserId, Long inviteeUserId) throws Exception {
    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/display-invitations/displays/{displayId}", displayId)
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

  private static String inviteRequestWithoutRole(Long inviteeUserId) {
    return """
        {
          "inviteeUserId": %d
        }
        """
        .formatted(inviteeUserId);
  }

  private static String acceptRequest(String displayNickname) {
    return """
        {
          "displayNickname": "%s"
        }
        """
        .formatted(displayNickname);
  }

  private static String updateNicknameRequest(Long displayId, String displayNickname) {
    return """
        {
          "displayId": %d,
          "displayNickname": "%s"
        }
        """
        .formatted(displayId, displayNickname);
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
            null, new UserId(leader.getId()), "전시 리더", TeamMemberRole.TEAM_LEADER, true));
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
