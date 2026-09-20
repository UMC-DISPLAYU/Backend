package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.entity.DisplayScreening;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;

public record DisplayScreeningDetailResult(
    Long displayId,
    String title,
    String subtitle,
    String content,
    Long requesterId,
    String status,
    Instant requestedAt,
    Instant processedAt,
    Long reviewerId,
    String rejectionReason,
    LocalDate startDate,
    LocalDate endDate,
    String location,
    List<String> imageUrls,
    String school,
    String leaderName) {

  public static DisplayScreeningDetailResult from(Display display, DisplayScreening screening) {
    return new DisplayScreeningDetailResult(
        display.getId(),
        display.getTitle(),
        display.getSubtitle(),
        display.getContent(),
        screening == null ? null : screening.getRequesterId(),
        display.getStatus().name(),
        screening == null ? null : toInstant(screening.getRequestedAt()),
        screening == null ? null : toInstant(screening.getProcessedAt()),
        screening == null ? null : screening.getReviewerId(),
        screening == null ? null : screening.getRejectionReason(),
        display.getPeriod().startDate(),
        display.getPeriod().endDate(),
        display.getLocation().placeName(),
        display.getImages().stream()
            .filter(image -> !image.isDeleted())
            .sorted(
                Comparator.comparing(
                        (DisplayImage image) -> image.getImageType() != DisplayImageType.MAIN)
                    .thenComparingInt(DisplayImage::getSortOrder))
            .map(DisplayImage::getImageUrl)
            .toList(),
        display.getOrganization(),
        uniqueLeaderName(display));
  }

  private static Instant toInstant(java.time.LocalDateTime value) {
    return value == null ? null : value.toInstant(ZoneOffset.UTC);
  }

  private static String uniqueLeaderName(Display display) {
    List<TeamMember> leaders =
        display.getTeamMembers().stream()
            .filter(TeamMember::isAccepted)
            .filter(member -> !member.isDeleted())
            .filter(member -> member.getRole() == TeamMemberRole.TEAM_LEADER)
            .toList();
    return leaders.size() == 1 ? leaders.getFirst().getDisplayNickname() : null;
  }
}
