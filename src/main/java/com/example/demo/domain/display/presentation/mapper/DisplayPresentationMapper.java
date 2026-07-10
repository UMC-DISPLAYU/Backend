package com.example.demo.domain.display.presentation.mapper;

import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.application.result.DisplayMapResult;
import com.example.demo.domain.display.application.result.DuPickResult;
import com.example.demo.domain.display.presentation.response.ClosingSoonDisplayResponse;
import com.example.demo.domain.display.presentation.response.DisplayDetailResponse;
import com.example.demo.domain.display.presentation.response.DisplayMapResponse;
import com.example.demo.domain.display.presentation.response.DuPickResponse;
import org.springframework.stereotype.Component;

@Component
public class DisplayPresentationMapper {

  public DuPickResponse toResponse(DuPickResult result) {
    return new DuPickResponse(
        result.duPicks().stream().map(this::toResponse).toList(),
        new DuPickResponse.CursorPaginationResponse(
            result.pagination().nextCursor(),
            result.pagination().size(),
            result.pagination().hasNext()));
  }

  public ClosingSoonDisplayResponse toResponse(ClosingSoonDisplayResult result) {
    return new ClosingSoonDisplayResponse(
        result.exhibitions().stream().map(this::toResponse).toList());
  }

  public DisplayMapResponse toResponse(DisplayMapResult result) {
    return new DisplayMapResponse(
        result.markers().stream().map(this::toResponse).toList(),
        new DisplayMapResponse.CursorPaginationResponse(
            result.pagination().nextCursor(),
            result.pagination().size(),
            result.pagination().hasNext()));
  }

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
        result.displayFields(),
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

  private DuPickResponse.DuPickItemResponse toResponse(DuPickResult.DuPickItemResult result) {
    return new DuPickResponse.DuPickItemResponse(
        result.duPickId(),
        result.title(),
        result.subtitle(),
        result.bannerImageUrl(),
        result.authorName(),
        result.createdAt());
  }

  private ClosingSoonDisplayResponse.ExhibitionResponse toResponse(
      ClosingSoonDisplayResult.ExhibitionResult result) {
    return new ClosingSoonDisplayResponse.ExhibitionResponse(
        result.displayId(),
        result.title(),
        result.posterImageUrl(),
        result.startedAt(),
        result.endedAt(),
        result.dayLeft());
  }

  private DisplayMapResponse.MarkerResponse toResponse(DisplayMapResult.MarkerResult result) {
    return new DisplayMapResponse.MarkerResponse(
        result.displayId(),
        result.title(),
        result.startDate(),
        result.endDate(),
        result.locationName(),
        result.posterImageUrl(),
        result.latitude(),
        result.longitude());
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
        result.sortOrder());
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
        result.invitationId(), result.inviterUserId(), result.inviteeUserId(), result.createdAt());
  }
}
