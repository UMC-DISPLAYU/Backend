package com.example.demo.domain.display.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.example.demo.domain.displayartwork.domain.aggregate.DisplayArtwork;
import com.example.demo.domain.displayartwork.domain.entity.ArtworkImage;
import com.example.demo.domain.displayartwork.domain.type.ArtworkImageType;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.CreatorRole;
import com.example.demo.domain.displayartwork.infrastructure.persistence.SpringDataDisplayArtworkJpaRepository;
import com.example.demo.global.security.JwtFactory;
import jakarta.persistence.EntityManager;
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
class DisplayMemberManagementControllerTest {

  private static final Long LEADER_USER_ID = 101L;
  private static final Long MEMBER_USER_ID = 102L;
  private static final Long MANAGER_USER_ID = 103L;

  @Autowired private MockMvc mockMvc;
  @Autowired private JwtFactory jwtFactory;
  @Autowired private SpringDataDisplayJpaRepository displayRepository;
  @Autowired private SpringDataDisplayArtworkJpaRepository artworkRepository;
  @Autowired private EntityManager entityManager;

  @Test
  void teamLeaderCanRemoveMemberAndDeletesOnlyArtworksLinkedByArtistAccount() throws Exception {
    Display display = displayRepository.saveAndFlush(display());
    TeamMember member = memberByUserId(display, MEMBER_USER_ID);
    DisplayArtwork sharedArtwork = persistArtwork(display, "공동 작품", MANAGER_USER_ID, 0);
    DisplayArtwork proxyRegistered = persistArtwork(display, "대리 등록 작품", MEMBER_USER_ID, 1);
    persistCreator(
        sharedArtwork.getId(), "다른 작가", CreatorRole.LEAD_ARTIST, MANAGER_USER_ID, 900001L);
    persistCreator(
        sharedArtwork.getId(), "나가는 공동 작가", CreatorRole.CO_AUTHOR, MEMBER_USER_ID, 900002L);
    persistCreator(proxyRegistered.getId(), "이름만 있는 작가", CreatorRole.LEAD_ARTIST, null, 900003L);
    persistCreator(
        proxyRegistered.getId(), "QnA 담당자", CreatorRole.QA_ONLY, MEMBER_USER_ID, 900004L);

    mockMvc
        .perform(
            delete(
                    "/api/v1/display/{displayId}/members/{memberId}",
                    display.getId(),
                    member.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.removedMemberId").value(member.getId()))
        .andExpect(jsonPath("$.success.data.removedUserId").value(MEMBER_USER_ID))
        .andExpect(jsonPath("$.success.data.deletedArtworkCount").value(1));

    entityManager.flush();
    entityManager.clear();
    assertThat(artworkRepository.findById(sharedArtwork.getId()).orElseThrow().isDeleted())
        .isTrue();
    assertThat(artworkRepository.findById(proxyRegistered.getId()).orElseThrow().isDeleted())
        .isFalse();
    assertThat(
            displayRepository.findById(display.getId()).orElseThrow().hasTeamMember(MEMBER_USER_ID))
        .isFalse();
  }

  @Test
  void removeMemberEnforcesLeaderPermissionAndRemovalPolicy() throws Exception {
    Display display = displayRepository.saveAndFlush(display());
    TeamMember leader = memberByUserId(display, LEADER_USER_ID);
    TeamMember member = memberByUserId(display, MEMBER_USER_ID);

    mockMvc
        .perform(
            delete(
                    "/api/v1/display/{displayId}/members/{memberId}",
                    display.getId(),
                    member.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(MANAGER_USER_ID)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));

    mockMvc
        .perform(
            delete(
                    "/api/v1/display/{displayId}/members/{memberId}",
                    display.getId(),
                    leader.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_LEADER_REMOVAL_NOT_ALLOWED"));

    mockMvc
        .perform(
            delete("/api/v1/display/{displayId}/members/{memberId}", display.getId(), 999999L)
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_MEMBER_NOT_FOUND"));

    mockMvc
        .perform(
            delete(
                "/api/v1/display/{displayId}/members/{memberId}", display.getId(), member.getId()))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void teamLeaderCanAssignAndReleaseManager() throws Exception {
    Display display = displayRepository.saveAndFlush(display());
    TeamMember member = memberByUserId(display, MEMBER_USER_ID);

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/members/{memberId}/role",
                    display.getId(),
                    member.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleRequest("MANAGER")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(jsonPath("$.success.data.teamMemberId").value(member.getId()))
        .andExpect(jsonPath("$.success.data.userId").value(MEMBER_USER_ID))
        .andExpect(jsonPath("$.success.data.role").value("MANAGER"));

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/members/{memberId}/role",
                    display.getId(),
                    member.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleRequest("TEAM_MEM")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.role").value("TEAM_MEM"));
  }

  @Test
  void managerCannotChangeMemberRole() throws Exception {
    Display display = displayRepository.saveAndFlush(display());
    TeamMember member = memberByUserId(display, MEMBER_USER_ID);

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/members/{memberId}/role",
                    display.getId(),
                    member.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(MANAGER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleRequest("MANAGER")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
  }

  @Test
  void roleChangeRejectsLeaderAndPendingMember() throws Exception {
    Display display = display();
    display.addTeamMember(
        new TeamMember(null, new UserId(104L), "대기 팀원", TeamMemberRole.TEAM_MEM, false));
    displayRepository.saveAndFlush(display);
    TeamMember leader = memberByUserId(display, LEADER_USER_ID);
    TeamMember pending = memberByUserId(display, 104L);

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/members/{memberId}/role",
                    display.getId(),
                    leader.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleRequest("MANAGER")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("INVALID_DISPLAY_MEMBER_ROLE_CHANGE"));

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/members/{memberId}/role",
                    display.getId(),
                    memberByUserId(display, MEMBER_USER_ID).getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleRequest("TEAM_LEADER")))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error.code").value("INVALID_DISPLAY_MEMBER_ROLE_CHANGE"));

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/members/{memberId}/role",
                    display.getId(),
                    pending.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleRequest("MANAGER")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_MEMBER_NOT_FOUND"));
  }

  @Test
  void roleChangeRejectsUnknownRoleAndUnauthenticatedRequest() throws Exception {
    Display display = displayRepository.saveAndFlush(display());
    TeamMember member = memberByUserId(display, MEMBER_USER_ID);

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/members/{memberId}/role",
                    display.getId(),
                    member.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleRequest("ADMIN")))
        .andExpect(status().isBadRequest());

    mockMvc
        .perform(
            patch(
                    "/api/v1/display/{displayId}/members/{memberId}/role",
                    display.getId(),
                    member.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleRequest("MANAGER")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
  }

  @Test
  void currentLeaderCanTransferLeadershipToManager() throws Exception {
    Display display = displayRepository.saveAndFlush(display());
    TeamMember previousLeader = memberByUserId(display, LEADER_USER_ID);
    TeamMember target = memberByUserId(display, MANAGER_USER_ID);

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/leader-transfer", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferRequest(target.getId())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.displayId").value(display.getId()))
        .andExpect(
            jsonPath("$.success.data.previousLeader.teamMemberId").value(previousLeader.getId()))
        .andExpect(jsonPath("$.success.data.previousLeader.role").value("MANAGER"))
        .andExpect(jsonPath("$.success.data.newLeader.teamMemberId").value(target.getId()))
        .andExpect(jsonPath("$.success.data.newLeader.role").value("TEAM_LEADER"));

    assertThat(display.isTeamLeader(MANAGER_USER_ID)).isTrue();
    assertThat(display.isManager(LEADER_USER_ID)).isTrue();
    assertThat(display.canInviteMember(LEADER_USER_ID)).isFalse();
  }

  @Test
  void managementApisReturnNotFoundForMissingDisplay() throws Exception {
    mockMvc
        .perform(
            patch("/api/v1/display/{displayId}/members/{memberId}/role", 999999L, 1L)
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleRequest("MANAGER")))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_NOT_FOUND"));

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/leader-transfer", 999999L)
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferRequest(1L)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_NOT_FOUND"));
  }

  @Test
  void leaderTransferRejectsNonLeaderSelfAndMissingMember() throws Exception {
    Display display = displayRepository.saveAndFlush(display());
    TeamMember leader = memberByUserId(display, LEADER_USER_ID);
    TeamMember member = memberByUserId(display, MEMBER_USER_ID);

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/leader-transfer", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(MEMBER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferRequest(member.getId())))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/leader-transfer", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferRequest(leader.getId())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.code").value("SELF_LEADER_TRANSFER_NOT_ALLOWED"));

    mockMvc
        .perform(
            post("/api/v1/display/{displayId}/leader-transfer", display.getId())
                .header(HttpHeaders.AUTHORIZATION, bearer(LEADER_USER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(transferRequest(999999L)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.code").value("DISPLAY_MEMBER_NOT_FOUND"));
  }

  private String bearer(Long userId) {
    return "Bearer " + jwtFactory.create(userId.toString(), 3_600_000L, "ACCESS");
  }

  private static String roleRequest(String role) {
    return """
        {"role": "%s"}
        """
        .formatted(role);
  }

  private static String transferRequest(Long targetMemberId) {
    return """
        {"targetMemberId": %d}
        """
        .formatted(targetMemberId);
  }

  private static TeamMember memberByUserId(Display display, Long userId) {
    return display.getTeamMembers().stream()
        .filter(member -> member.getUserId().value().equals(userId))
        .findFirst()
        .orElseThrow();
  }

  private DisplayArtwork persistArtwork(
      Display display, String artworkName, Long registeredByUserId, int sortOrder) {
    DisplayArtwork artwork =
        DisplayArtwork.create(
            display,
            artworkName,
            "설명",
            List.of(ArtworkType.PAINTING),
            2026,
            "재료",
            "10 x 10 cm",
            "감상 포인트",
            sortOrder,
            registeredByUserId,
            List.of(
                new ArtworkImage(
                    null,
                    "https://cdn.displayu.com/artworks/" + artworkName + ".png",
                    true,
                    ArtworkImageType.ARTWORK,
                    0,
                    null,
                    100,
                    100)));
    return artworkRepository.saveAndFlush(artwork);
  }

  private void persistCreator(
      Long artworkId, String name, CreatorRole role, Long userId, Long creatorId) {
    entityManager
        .createNativeQuery(
            """
            INSERT INTO creator
              (creator_id, display_artwork_id, creator_name, is_contact, is_leader, role, user_id)
            VALUES (?, ?, ?, FALSE, ?, ?, ?)
            """)
        .setParameter(1, creatorId)
        .setParameter(2, artworkId)
        .setParameter(3, name)
        .setParameter(4, role == CreatorRole.LEAD_ARTIST)
        .setParameter(5, role.name())
        .setParameter(6, userId)
        .executeUpdate();
    entityManager.flush();
    entityManager.clear();
  }

  private static Display display() {
    Display display =
        Display.create(
            new UserId(LEADER_USER_ID),
            "관리자 API 테스트 전시",
            "https://cdn.displayu.com/poster.png",
            "subtitle",
            "content",
            new DisplayLocation("전시장", BigDecimal.ONE, BigDecimal.ONE),
            "",
            "",
            "organization",
            "department",
            DisplayType.GRADUATION,
            List.of(DisplayField.DESIGN),
            DisplayRegion.SEOUL,
            new DisplayPeriod(
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 2),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0)),
            ContentOpenPolicy.IMMEDIATELY,
            ContentOpenPolicy.IMMEDIATELY);
    display.addTeamMember(
        new TeamMember(null, new UserId(LEADER_USER_ID), "대표자", TeamMemberRole.TEAM_LEADER, true));
    display.addTeamMember(
        new TeamMember(null, new UserId(MEMBER_USER_ID), "일반 팀원", TeamMemberRole.TEAM_MEM, true));
    display.addTeamMember(
        new TeamMember(null, new UserId(MANAGER_USER_ID), "관리자", TeamMemberRole.MANAGER, true));
    return display;
  }
}
