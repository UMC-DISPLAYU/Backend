package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.TeamMember;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

public record DisplayReviewAccessResult(
    Long ownerUserId,
    LocalDate startDate,
    LocalDate endDate,
    boolean published,
    Set<Long> acceptedTeamMemberUserIds) {

  public DisplayReviewAccessResult {
    acceptedTeamMemberUserIds = Set.copyOf(acceptedTeamMemberUserIds);
  }

  public static DisplayReviewAccessResult from(Display display) {
    Set<Long> acceptedTeamMemberUserIds =
        display.getTeamMembers().stream()
            .filter(TeamMember::isAccepted)
            .filter(teamMember -> !teamMember.isDeleted())
            .map(teamMember -> teamMember.getUserId().value())
            .collect(Collectors.toUnmodifiableSet());

    return new DisplayReviewAccessResult(
        display.getOwnerUserId().value(),
        display.getPeriod().startDate(),
        display.getPeriod().endDate(),
        display.isPublished(),
        acceptedTeamMemberUserIds);
  }

  public boolean isOwnerOrAcceptedTeamMember(Long userId) {
    return ownerUserId.equals(userId) || acceptedTeamMemberUserIds.contains(userId);
  }
}
