package com.example.demo.domain.display.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WithdrawalDisplayRepositoryTest {

  private static final Long ACTIVE_LEADER_ID = 8101L;
  private static final Long DELETED_DISPLAY_LEADER_ID = 8102L;
  private static final Long MIXED_DISPLAY_LEADER_ID = 8103L;
  private static final Long FORMER_LEADER_ID = 8201L;
  private static final Long CURRENT_LEADER_ID = 8202L;

  @Autowired private SpringDataDisplayJpaRepository displayRepository;
  @Autowired private SpringDataTeamMemberJpaRepository teamMemberRepository;

  @Test
  void leaderWithdrawalBlockConsidersOnlyActiveDisplaysAndActiveAcceptedMembers() {
    displayRepository.save(displayWithLeader("active", ACTIVE_LEADER_ID));

    Display deletedDisplay = displayWithLeader("deleted", DELETED_DISPLAY_LEADER_ID);
    deletedDisplay.delete();
    displayRepository.save(deletedDisplay);

    Display mixedDeletedDisplay = displayWithLeader("mixed-deleted", MIXED_DISPLAY_LEADER_ID);
    mixedDeletedDisplay.delete();
    displayRepository.save(mixedDeletedDisplay);
    displayRepository.save(displayWithLeader("mixed-active", MIXED_DISPLAY_LEADER_ID));

    Display inactiveMembers = display("inactive-members", 8999L);
    inactiveMembers.addTeamMember(
        new TeamMember(null, new UserId(8301L), "미승인 대표자", TeamMemberRole.TEAM_LEADER, false));
    TeamMember deletedLeader =
        new TeamMember(null, new UserId(8302L), "삭제 대표자", TeamMemberRole.TEAM_LEADER, true);
    deletedLeader.softDelete(LocalDateTime.of(2026, 11, 14, 12, 0));
    inactiveMembers.addTeamMember(deletedLeader);
    displayRepository.saveAndFlush(inactiveMembers);

    assertThat(
            teamMemberRepository.existsActiveAcceptedByUserIdAndRole(
                ACTIVE_LEADER_ID, TeamMemberRole.TEAM_LEADER))
        .isTrue();
    assertThat(
            teamMemberRepository.existsActiveAcceptedByUserIdAndRole(
                DELETED_DISPLAY_LEADER_ID, TeamMemberRole.TEAM_LEADER))
        .isFalse();
    assertThat(
            teamMemberRepository.existsActiveAcceptedByUserIdAndRole(
                MIXED_DISPLAY_LEADER_ID, TeamMemberRole.TEAM_LEADER))
        .isTrue();
    assertThat(
            teamMemberRepository.existsActiveAcceptedByUserIdAndRole(
                8301L, TeamMemberRole.TEAM_LEADER))
        .isFalse();
    assertThat(
            teamMemberRepository.existsActiveAcceptedByUserIdAndRole(
                8302L, TeamMemberRole.TEAM_LEADER))
        .isFalse();
  }

  @Test
  void withdrawalQueryIncludesFormerOwnerLeaderButScreenQueryStillExcludesOwner() {
    Display active = display("transferred", FORMER_LEADER_ID);
    active.addTeamMember(
        new TeamMember(
            null, new UserId(FORMER_LEADER_ID), "이전 대표자", TeamMemberRole.TEAM_LEADER, true));
    active.addTeamMember(
        new TeamMember(
            null, new UserId(CURRENT_LEADER_ID), "현재 대표자", TeamMemberRole.MANAGER, true));
    active = displayRepository.saveAndFlush(active);
    Long currentLeaderMemberId = memberByUserId(active, CURRENT_LEADER_ID).getId();
    active.transferLeadership(FORMER_LEADER_ID, currentLeaderMemberId);

    Display deleted = displayWithLeader("deleted-target", FORMER_LEADER_ID);
    memberByUserId(deleted, FORMER_LEADER_ID).changeRole(TeamMemberRole.MANAGER);
    deleted.delete();
    displayRepository.saveAndFlush(deleted);

    List<Display> withdrawalTargets =
        displayRepository.findWithdrawalTargetDisplaysByUserId(FORMER_LEADER_ID);
    List<Display> screenParticipations =
        displayRepository.findParticipatedDisplaysByUserId(FORMER_LEADER_ID);

    assertThat(withdrawalTargets).extracting(Display::getId).containsExactly(active.getId());
    assertThat(screenParticipations).isEmpty();
    assertThat(memberByUserId(active, FORMER_LEADER_ID).getRole())
        .isEqualTo(TeamMemberRole.MANAGER);
    assertThat(memberByUserId(active, CURRENT_LEADER_ID).getRole())
        .isEqualTo(TeamMemberRole.TEAM_LEADER);
  }

  private static Display displayWithLeader(String title, Long leaderUserId) {
    Display display = display(title, leaderUserId);
    display.addTeamMember(
        new TeamMember(null, new UserId(leaderUserId), "대표자", TeamMemberRole.TEAM_LEADER, true));
    return display;
  }

  private static Display display(String title, Long ownerUserId) {
    return Display.create(
        new UserId(ownerUserId),
        title,
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
  }

  private static TeamMember memberByUserId(Display display, Long userId) {
    return display.getTeamMembers().stream()
        .filter(member -> member.getUserId().value().equals(userId))
        .findFirst()
        .orElseThrow();
  }
}
