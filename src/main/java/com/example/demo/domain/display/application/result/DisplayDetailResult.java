package com.example.demo.domain.display.application.result;

import com.example.demo.domain.display.domain.aggregate.Display;
import com.example.demo.domain.display.domain.entity.DisplayContent;
import com.example.demo.domain.display.domain.entity.DisplayContentCategory;
import com.example.demo.domain.display.domain.entity.DisplayImage;
import com.example.demo.domain.display.domain.entity.DisplayInvitation;
import com.example.demo.domain.display.domain.entity.TeamMember;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record DisplayDetailResult(
    Long displayId,
    Long ownerUserId,
    String title,
    String subtitle,
    String content,
    LocationResult location,
    String qnaAccount,
    String note,
    String organization,
    String department,
    String displayType,
    String displayField,
    PeriodResult period,
    String artworkContentOpen,
    String exhibitionContentOpen,
    String status,
    String invitationToken,
    LocalDateTime invitationDisabledAt,
    List<ImageResult> images,
    List<ContentCategoryResult> contentCategories,
    List<TeamMemberResult> teamMembers,
    List<InvitationResult> invitations) {

  public static DisplayDetailResult from(Display display) {
    return new DisplayDetailResult(
        display.getId(),
        display.getOwnerUserId().value(),
        display.getTitle(),
        display.getSubtitle(),
        display.getContent(),
        LocationResult.from(display),
        display.getQnaAccount(),
        display.getNote(),
        display.getOrganization(),
        display.getDepartment(),
        display.getDisplayType().name(),
        display.getDisplayField().name(),
        PeriodResult.from(display),
        display.getArtworkContentOpen().name(),
        display.getExhibitionContentOpen().name(),
        display.getStatus().name(),
        display.getInvitationToken(),
        display.getInvitationDisabledAt(),
        display.getImages().stream().map(ImageResult::from).toList(),
        display.getContentCategories().stream().map(ContentCategoryResult::from).toList(),
        display.getTeamMembers().stream().map(TeamMemberResult::from).toList(),
        display.getInvitations().stream().map(InvitationResult::from).toList());
  }

  public record LocationResult(String placeName, BigDecimal latitude, BigDecimal longitude) {

    private static LocationResult from(Display display) {
      return new LocationResult(
          display.getLocation().placeName(),
          display.getLocation().latitude(),
          display.getLocation().longitude());
    }
  }

  public record PeriodResult(
      LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {

    private static PeriodResult from(Display display) {
      return new PeriodResult(
          display.getPeriod().startDate(),
          display.getPeriod().endDate(),
          display.getPeriod().startTime(),
          display.getPeriod().endTime());
    }
  }

  public record ImageResult(
      Long imageId,
      String imageUrl,
      String imageType,
      int width,
      int height,
      int sortOrder,
      LocalDateTime deletedAt) {

    private static ImageResult from(DisplayImage image) {
      return new ImageResult(
          image.getId(),
          image.getImageUrl(),
          image.getImageType().name(),
          image.getWidth(),
          image.getHeight(),
          image.getSortOrder(),
          image.getDeletedAt());
    }
  }

  public record ContentCategoryResult(
      Long categoryId,
      String name,
      String description,
      int sortOrder,
      List<ContentResult> contents) {

    private static ContentCategoryResult from(DisplayContentCategory category) {
      return new ContentCategoryResult(
          category.getId(),
          category.getName(),
          category.getDescription(),
          category.getSortOrder(),
          category.getContents().stream().map(ContentResult::from).toList());
    }
  }

  public record ContentResult(
      Long contentId, String imageUrl, int width, int height, int sortOrder) {

    private static ContentResult from(DisplayContent content) {
      return new ContentResult(
          content.getId(),
          content.getImageUrl(),
          content.getWidth(),
          content.getHeight(),
          content.getSortOrder());
    }
  }

  public record TeamMemberResult(
      Long teamMemberId, Long userId, String displayNickname, String role, boolean accepted) {

    private static TeamMemberResult from(TeamMember teamMember) {
      return new TeamMemberResult(
          teamMember.getId(),
          teamMember.getUserId().value(),
          teamMember.getDisplayNickname(),
          teamMember.getRole().name(),
          teamMember.isAccepted());
    }
  }

  public record InvitationResult(
      Long invitationId,
      Long inviterUserId,
      Long inviteeUserId,
      LocalDateTime createdAt,
      LocalDateTime deletedAt) {

    private static InvitationResult from(DisplayInvitation invitation) {
      return new InvitationResult(
          invitation.getId(),
          invitation.getInviterUserId().value(),
          invitation.getInviteeUserId().value(),
          invitation.getCreatedAt(),
          invitation.getDeletedAt());
    }
  }
}
