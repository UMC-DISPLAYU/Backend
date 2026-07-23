package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.entity.TeamMember;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.display.domain.type.TeamMemberRole;
import java.time.LocalDate;
import java.util.List;

public record MyDisplayInvitationListResult(List<InvitationResult> invitations) {

  public static MyDisplayInvitationListResult from(List<DisplayInvitation> invitations) {
    return new MyDisplayInvitationListResult(
        invitations.stream().map(InvitationResult::from).toList());
  }

  public record InvitationResult(
      Long invitationId,
      Long displayId,
      String thumbnailUrl,
      LocalDate startDate,
      LocalDate endDate,
      String location,
      String leaderName,
      String title,
      String placeName) {

    private static InvitationResult from(DisplayInvitation invitation) {
      Display display = invitation.getDisplay();
      return new InvitationResult(
          invitation.getId(),
          display.getId(),
          thumbnailUrl(display),
          display.getPeriod().startDate(),
          display.getPeriod().endDate(),
          display.getRegion().name(),
          leaderName(display),
          display.getTitle(),
          display.getLocation().placeName());
    }

    private static String thumbnailUrl(Display display) {
      return display.getImages().stream()
          .filter(image -> image.getImageType() == DisplayImageType.MAIN)
          .filter(image -> !image.isDeleted())
          .findFirst()
          .map(DisplayImage::getImageUrl)
          .orElse(null);
    }

    private static String leaderName(Display display) {
      return display.getTeamMembers().stream()
          .filter(TeamMember::isAccepted)
          .filter(teamMember -> teamMember.getRole() == TeamMemberRole.TEAM_LEADER)
          .findFirst()
          .map(TeamMember::getDisplayNickname)
          .orElse(null);
    }
  }
}
