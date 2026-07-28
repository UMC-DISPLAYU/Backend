package com.example.demo.domain.display.presentation.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record DisplayDetailResponse(
    Long displayId,
    Long ownerUserId,
    String title,
    String subtitle,
    String content,
    LocationResponse location,
    String qnaAccount,
    String note,
    String organization,
    String department,
    String displayType,
    List<String> displayFields,
    String region,
    long likeCount,
    PeriodResponse period,
    String artworkContentOpen,
    String exhibitionContentOpen,
    String status,
    String invitationToken,
    LocalDateTime invitationDisabledAt,
    List<ImageResponse> images,
    List<ContentCategoryResponse> contentCategories,
    List<TeamMemberResponse> teamMembers,
    List<InvitationResponse> invitations) {

  public record LocationResponse(String placeName, BigDecimal latitude, BigDecimal longitude) {}

  public record PeriodResponse(
      LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {}

  public record ImageResponse(
      Long imageId, String imageUrl, String imageType, int width, int height, int sortOrder) {}

  public record ContentCategoryResponse(
      Long categoryId,
      String name,
      String description,
      int sortOrder,
      List<ContentResponse> contents) {}

  public record ContentResponse(
      Long contentId, String imageUrl, int width, int height, int sortOrder) {}

  public record TeamMemberResponse(
      Long teamMemberId, Long userId, String displayNickname, String role, boolean accepted) {}

  public record InvitationResponse(
      Long invitationId, Long inviterUserId, Long inviteeUserId, LocalDateTime createdAt) {}
}
