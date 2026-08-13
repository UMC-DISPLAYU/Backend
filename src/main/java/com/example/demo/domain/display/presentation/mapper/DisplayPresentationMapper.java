package com.example.demo.domain.display.presentation.mapper;

import com.example.demo.domain.display.application.command.CreateDisplayCommand;
import com.example.demo.domain.display.application.command.UpdateDisplayCommand;
import com.example.demo.domain.display.application.result.ClosingSoonDisplayResult;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.application.result.DisplayInvitationResult;
import com.example.demo.domain.display.application.result.DisplayInvitationStatusResult;
import com.example.demo.domain.display.application.result.DisplayLikeResult;
import com.example.demo.domain.display.application.result.DisplayLikeStatusResult;
import com.example.demo.domain.display.application.result.DisplayMapResult;
import com.example.demo.domain.display.application.result.DuPickResult;
import com.example.demo.domain.display.application.result.GraduationDisplayResult;
import com.example.demo.domain.display.application.result.MyDisplayListResult;
import com.example.demo.domain.display.application.result.SearchDisplayResult;
import com.example.demo.domain.display.domain.type.ContentOpenPolicy;
import com.example.demo.domain.display.domain.type.DisplayField;
import com.example.demo.domain.display.domain.type.DisplayRegion;
import com.example.demo.domain.display.domain.type.DisplayType;
import com.example.demo.domain.display.presentation.request.CreateDisplayRequest;
import com.example.demo.domain.display.presentation.request.UpdateDisplayRequest;
import com.example.demo.domain.display.presentation.response.ClosingSoonDisplayResponse;
import com.example.demo.domain.display.presentation.response.DisplayDetailResponse;
import com.example.demo.domain.display.presentation.response.DisplayInvitationResponse;
import com.example.demo.domain.display.presentation.response.DisplayInvitationStatusResponse;
import com.example.demo.domain.display.presentation.response.DisplayLikeResponse;
import com.example.demo.domain.display.presentation.response.DisplayLikeStatusResponse;
import com.example.demo.domain.display.presentation.response.DisplayMapResponse;
import com.example.demo.domain.display.presentation.response.DuPickResponse;
import com.example.demo.domain.display.presentation.response.GraduationDisplayResponse;
import com.example.demo.domain.display.presentation.response.MyDisplayListResponse;
import com.example.demo.domain.display.presentation.response.SearchDisplayResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DisplayPresentationMapper {

  public CreateDisplayCommand toCommand(CreateDisplayRequest request, Long ownerUserId) {
    return new CreateDisplayCommand(
        ownerUserId,
        request.title(),
        request.posterImageUrl(),
        request.displayImageUrls() == null ? List.of() : request.displayImageUrls(),
        request.subtitle(),
        request.description(),
        request.locationName(),
        request.latitude(),
        request.longitude(),
        request.roadAddress(),
        request.qnaAccount(),
        request.precautions(),
        organization(request),
        department(request),
        request.displayNickname(),
        request.contract(),
        toDisplayType(request.type()),
        request.fields().stream().map(this::toDisplayField).toList(),
        toDisplayRegion(request.region()),
        request.startDate(),
        request.endDate(),
        request.openTime(),
        request.closeTime(),
        ContentOpenPolicy.IMMEDIATELY,
        ContentOpenPolicy.ON_EXHIBITION);
  }

  public UpdateDisplayCommand toCommand(UpdateDisplayRequest request, Long userId) {
    return new UpdateDisplayCommand(
        userId,
        request.displayId(),
        request.title(),
        request.posterImageUrl(),
        request.type() == null ? null : toDisplayType(request.type()),
        request.fields() == null
            ? null
            : request.fields().stream().map(this::toDisplayField).toList(),
        request.schoolOrOrganization(),
        request.departmentOrClub(),
        request.hostOrganizationName(),
        request.contract(),
        request.subtitle(),
        request.description(),
        request.startDate(),
        request.endDate(),
        request.openTime(),
        request.closeTime(),
        request.placeName(),
        request.precautions());
  }

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
        result.exhibitions().stream().map(this::toResponse).toList(),
        new ClosingSoonDisplayResponse.CursorPaginationResponse(
            result.pagination().nextCursor(),
            result.pagination().size(),
            result.pagination().hasNext()));
  }

  public GraduationDisplayResponse toResponse(GraduationDisplayResult result) {
    return new GraduationDisplayResponse(
        result.exhibitions().stream().map(this::toResponse).toList());
  }

  public SearchDisplayResponse toResponse(SearchDisplayResult result) {
    return new SearchDisplayResponse(
        result.exhibitions().stream().map(this::toResponse).toList(),
        new SearchDisplayResponse.CursorPaginationResponse(
            result.pagination().nextCursor(),
            result.pagination().size(),
            result.pagination().hasNext()));
  }

  public DisplayMapResponse toResponse(DisplayMapResult result) {
    return new DisplayMapResponse(
        result.markers().stream().map(this::toResponse).toList(),
        new DisplayMapResponse.CursorPaginationResponse(
            result.pagination().nextCursor(),
            result.pagination().size(),
            result.pagination().hasNext()));
  }

  public MyDisplayListResponse toResponse(MyDisplayListResult result) {
    return new MyDisplayListResponse(
        result.createdDisplays().stream().map(this::toResponse).toList(),
        result.participatedDisplays().stream().map(this::toResponse).toList());
  }

  public DisplayLikeResponse toResponse(DisplayLikeResult result) {
    return new DisplayLikeResponse(result.displayId(), result.likeCount());
  }

  public DisplayLikeStatusResponse toResponse(DisplayLikeStatusResult result) {
    return new DisplayLikeStatusResponse(result.isLiked());
  }

  public DisplayInvitationResponse toResponse(DisplayInvitationResult result) {
    return new DisplayInvitationResponse(result.displayId(), result.invitationUrl());
  }

  public DisplayInvitationStatusResponse toResponse(DisplayInvitationStatusResult result) {
    return new DisplayInvitationStatusResponse(
        result.displayId(),
        result.enabled(),
        result.invitationUrl(),
        result.invitationDisabledAt());
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
        result.contract(),
        result.note(),
        result.organization(),
        result.department(),
        result.displayType(),
        result.displayFields(),
        result.region(),
        result.likeCount(),
        result.isArchived(),
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
        result.createdAt());
  }

  private ClosingSoonDisplayResponse.ExhibitionResponse toResponse(
      ClosingSoonDisplayResult.ExhibitionResult result) {
    return new ClosingSoonDisplayResponse.ExhibitionResponse(
        result.displayId(),
        result.title(),
        result.posterImageUrl(),
        schoolDepartmentName(result.organization(), result.department()),
        result.startedAt(),
        result.endedAt(),
        result.dayLeft(),
        result.isArchived());
  }

  private GraduationDisplayResponse.ExhibitionResponse toResponse(
      GraduationDisplayResult.ExhibitionResult result) {
    return new GraduationDisplayResponse.ExhibitionResponse(
        result.displayId(),
        result.title(),
        result.posterImageUrl(),
        schoolDepartmentName(result.organization(), result.department()),
        result.startedAt(),
        result.endedAt(),
        result.dayLeft(),
        result.isArchived());
  }

  private SearchDisplayResponse.ExhibitionResponse toResponse(
      SearchDisplayResult.ExhibitionResult result) {
    return new SearchDisplayResponse.ExhibitionResponse(
        result.displayId(),
        result.title(),
        result.posterImageUrl(),
        schoolDepartmentName(result.organization(), result.department()),
        result.startedAt(),
        result.endedAt(),
        result.dayLeft(),
        result.isArchived());
  }

  private DisplayMapResponse.MarkerResponse toResponse(DisplayMapResult.MarkerResult result) {
    return new DisplayMapResponse.MarkerResponse(
        result.displayId(),
        result.title(),
        result.startDate(),
        result.endDate(),
        result.locationName(),
        result.posterImageUrl(),
        schoolDepartmentName(result.organization(), result.department()),
        result.latitude(),
        result.longitude(),
        result.isArchived());
  }

  private MyDisplayListResponse.MyDisplayResponse toResponse(
      MyDisplayListResult.MyDisplayResult result) {
    return new MyDisplayListResponse.MyDisplayResponse(
        result.displayId(),
        result.title(),
        result.displayStatus(),
        result.publishStatus(),
        result.startDate(),
        result.endDate(),
        result.school(),
        result.department(),
        result.placeName(),
        result.postImageUrl(),
        result.isLeader());
  }

  private DisplayDetailResponse.LocationResponse toResponse(
      DisplayDetailResult.LocationResult result) {
    return new DisplayDetailResponse.LocationResponse(
        result.placeName(), result.latitude(), result.longitude(), result.roadAddress());
  }

  private DisplayDetailResponse.PeriodResponse toResponse(DisplayDetailResult.PeriodResult result) {
    return new DisplayDetailResponse.PeriodResponse(
        result.startDate(), result.endDate(), result.startTime(), result.endTime());
  }

  private DisplayDetailResponse.ImageResponse toResponse(DisplayDetailResult.ImageResult result) {
    return new DisplayDetailResponse.ImageResponse(
        result.imageId(), result.imageUrl(), result.imageType(), result.sortOrder());
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
        result.contentId(), result.imageUrl(), result.sortOrder());
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

  private String schoolDepartmentName(String organization, String department) {
    String trimmedOrganization = trimToEmpty(organization);
    String trimmedDepartment = trimToEmpty(department);
    if (trimmedOrganization.isBlank()) {
      return trimmedDepartment;
    }
    if (trimmedDepartment.isBlank()) {
      return trimmedOrganization;
    }
    return trimmedOrganization + " " + trimmedDepartment;
  }

  private String trimToEmpty(String value) {
    return value == null ? "" : value.trim();
  }

  private String organization(CreateDisplayRequest request) {
    return request.schoolOrOrganization();
  }

  private String department(CreateDisplayRequest request) {
    return requiresSchoolInfo(request.type()) ? request.departmentOrClub() : "";
  }

  private boolean requiresSchoolInfo(CreateDisplayRequest.Type type) {
    return type == CreateDisplayRequest.Type.GRADUATION || type == CreateDisplayRequest.Type.TASK;
  }

  private DisplayType toDisplayType(CreateDisplayRequest.Type type) {
    return switch (type) {
      case GRADUATION -> DisplayType.GRADUATION;
      case TASK -> DisplayType.ASSIGNMENTS;
      case CLUB -> DisplayType.SMALL_GROUP;
      case JOINT -> DisplayType.INTER_GROUP;
      case ETC -> DisplayType.OTHERS;
    };
  }

  private DisplayType toDisplayType(UpdateDisplayRequest.Type type) {
    return switch (type) {
      case GRADUATION -> DisplayType.GRADUATION;
      case TASK -> DisplayType.ASSIGNMENTS;
      case CLUB -> DisplayType.SMALL_GROUP;
      case JOINT -> DisplayType.INTER_GROUP;
      case ETC -> DisplayType.OTHERS;
    };
  }

  private DisplayField toDisplayField(CreateDisplayRequest.Field field) {
    return switch (field) {
      case PAINTING -> DisplayField.PAINTING;
      case DESIGN -> DisplayField.DESIGN;
      case PHOTOGRAPHY -> DisplayField.PHOTOGRAPHY;
      case ARCHITECTURE -> DisplayField.ARCHITECTURE;
      case MEDIA -> DisplayField.VIDEO;
      case CRAFT -> DisplayField.CRAFTS;
      case SCULPTURE -> DisplayField.SCULPTURE;
      case FASHION -> DisplayField.FASHION;
      case COMPLEX -> DisplayField.INTERDISCIPLINARY;
      case ETC -> DisplayField.OTHERS;
    };
  }

  private DisplayField toDisplayField(UpdateDisplayRequest.Field field) {
    return switch (field) {
      case PAINTING -> DisplayField.PAINTING;
      case DESIGN -> DisplayField.DESIGN;
      case PHOTOGRAPHY -> DisplayField.PHOTOGRAPHY;
      case ARCHITECTURE -> DisplayField.ARCHITECTURE;
      case MEDIA -> DisplayField.VIDEO;
      case CRAFT -> DisplayField.CRAFTS;
      case SCULPTURE -> DisplayField.SCULPTURE;
      case FASHION -> DisplayField.FASHION;
      case COMPLEX -> DisplayField.INTERDISCIPLINARY;
      case ETC -> DisplayField.OTHERS;
    };
  }

  private DisplayRegion toDisplayRegion(CreateDisplayRequest.Region region) {
    return switch (region) {
      case ALL -> DisplayRegion.ALL;
      case SEOUL -> DisplayRegion.SEOUL;
      case GYEONGGI_INCHEON -> DisplayRegion.GYEONGGI_INCHEON;
      case OTHERS -> DisplayRegion.OTHERS;
    };
  }
}
