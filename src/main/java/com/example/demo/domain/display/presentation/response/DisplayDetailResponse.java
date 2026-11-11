package com.example.demo.domain.display.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
    String contract,
    String note,
    String organization,
    String department,
    String displayType,
    List<String> displayFields,
    String region,
    long likeCount,
    @JsonProperty("isArchived") boolean isArchived,
    PeriodResponse period,
    String artworkContentOpen,
    String exhibitionContentOpen,
    String status,
    String invitationToken,
    LocalDateTime invitationDisabledAt,
    List<ImageResponse> images,
    List<ContentCategoryResponse> contentCategories,
    List<TeamMemberResponse> teamMembers,
    List<InvitationResponse> invitations,
    @Schema(
            description = "현재 저장된 Instagram URL",
            types = {"string", "null"},
            maxLength = 255)
        String instagramUrl,
    @Schema(
            description = "현재 저장된 웹사이트 URL",
            types = {"string", "null"},
            maxLength = 255)
        String websiteUrl,
    @Schema(
            description = "현재 심사 중인 요청 ID. 심사 중이 아니면 null이며 취소 API의 screeningId로 사용합니다.",
            types = {"integer", "null"})
        Long currentScreeningId,
    @Schema(
            types = {"object", "null"},
            description = "대표 포스터 표시 범위. null이면 조정 없음.")
        PosterCropResponse posterCrop) {

  public record LocationResponse(
      String placeName, BigDecimal latitude, BigDecimal longitude, String roadAddress) {}

  public record PeriodResponse(
      LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {}

  public record ImageResponse(Long imageId, String imageUrl, String imageType, int sortOrder) {}

  public record ContentCategoryResponse(
      Long categoryId,
      String name,
      String description,
      int sortOrder,
      List<ContentResponse> contents) {}

  public record ContentResponse(Long contentId, Long userId, String imageUrl, int sortOrder) {}

  public record TeamMemberResponse(
      Long teamMemberId, Long userId, String displayNickname, String role, boolean accepted) {}

  public record InvitationResponse(
      Long invitationId, Long inviterUserId, Long inviteeUserId, LocalDateTime createdAt) {}
}
