package com.example.demo.domain.display.presentation.mapper;

import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.presentation.response.DisplayDetailResponse;
import org.springframework.stereotype.Component;

@Component
public class DisplayPresentationMapper {

  public DisplayDetailResponse toResponse(DisplayDetailResult result) {
    return new DisplayDetailResponse(
        result.displayId(),
        result.ownerUserId(),
        result.title(),
        result.subtitle(),
        result.content(),
        toResponse(result.location()),
        result.qnaAccount(),
        result.note(),
        result.organization(),
        result.department(),
        result.displayType(),
        result.displayField(),
        toResponse(result.period()),
        result.artworkContentOpen(),
        result.exhibitionContentOpen(),
        result.status(),
        result.invitationToken(),
        result.invitationDisabledAt(),
        result.images().stream().map(this::toResponse).toList(),
        result.contentCategories().stream().map(this::toResponse).toList(),
        result.teamMembers().stream().map(this::toResponse).toList(),
        result.invitations().stream().map(this::toResponse).toList());
  }

  private DisplayDetailResponse.LocationResponse toResponse(
      DisplayDetailResult.LocationResult result) {
    return new DisplayDetailResponse.LocationResponse(
        result.placeName(), result.latitude(), result.longitude());
  }

  private DisplayDetailResponse.PeriodResponse toResponse(DisplayDetailResult.PeriodResult result) {
    return new DisplayDetailResponse.PeriodResponse(
        result.startDate(), result.endDate(), result.startTime(), result.endTime());
  }

  private DisplayDetailResponse.ImageResponse toResponse(DisplayDetailResult.ImageResult result) {
    return new DisplayDetailResponse.ImageResponse(
        result.imageId(),
        result.imageUrl(),
        result.imageType(),
        result.width(),
        result.height(),
        result.sortOrder(),
        result.deletedAt());
  }

  private DisplayDetailResponse.ContentCategoryResponse toResponse(
      DisplayDetailResult.ContentCategoryResult result) {
    return new DisplayDetailResponse.ContentCategoryResponse(
        result.categoryId(),
        result.name(),
        result.description(),
        result.sortOrder(),
        result.contents().stream().map(this::toResponse).toList());
  }

  private DisplayDetailResponse.ContentResponse toResponse(
      DisplayDetailResult.ContentResult result) {
    return new DisplayDetailResponse.ContentResponse(
        result.contentId(), result.imageUrl(), result.width(), result.height(), result.sortOrder());
  }

  private DisplayDetailResponse.TeamMemberResponse toResponse(
      DisplayDetailResult.TeamMemberResult result) {
    return new DisplayDetailResponse.TeamMemberResponse(
        result.teamMemberId(),
        result.userId(),
        result.displayNickname(),
        result.role(),
        result.accepted());
  }

  private DisplayDetailResponse.InvitationResponse toResponse(
      DisplayDetailResult.InvitationResult result) {
    return new DisplayDetailResponse.InvitationResponse(
        result.invitationId(),
        result.inviterUserId(),
        result.inviteeUserId(),
        result.createdAt(),
        result.deletedAt());
  }
}
