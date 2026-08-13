package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.type.DisplayImageType;
import com.example.demo.domain.user.domain.aggregate.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record MyDisplayInvitationListResult(List<InvitationResult> invitations) {

  public static MyDisplayInvitationListResult from(
      List<DisplayInvitation> invitations,
      Map<Long, User> invitersById,
      Map<Long, String> leaderNamesByDisplayId) {
    return new MyDisplayInvitationListResult(
        invitations.stream()
            .map(
                invitation ->
                    InvitationResult.from(invitation, invitersById, leaderNamesByDisplayId))
            .toList());
  }

  public record InvitationResult(
      Long invitationId,
      Long displayId,
      String thumbnailUrl,
      LocalDate startDate,
      LocalDate endDate,
      String location,
      String userNickname,
      String leaderName,
      String title,
      String school,
      String department,
      String placeName) {

    private static InvitationResult from(
        DisplayInvitation invitation,
        Map<Long, User> invitersById,
        Map<Long, String> leaderNamesByDisplayId) {
      Display display = invitation.getDisplay();
      User inviter = invitersById.get(invitation.getInviterUserId().value());
      return new InvitationResult(
          invitation.getId(),
          display.getId(),
          thumbnailUrl(display),
          display.getPeriod().startDate(),
          display.getPeriod().endDate(),
          display.getRegion().name(),
          inviter != null ? inviter.getNickname() : "",
          leaderNamesByDisplayId.getOrDefault(display.getId(), ""),
          display.getTitle(),
          display.getOrganization(),
          display.getDepartment(),
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
  }
}
