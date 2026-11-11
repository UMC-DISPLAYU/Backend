package com.example.demo.domain.admin.presentation.response;

import com.example.demo.domain.display.presentation.response.PosterCropResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class ReviewResponses {
  private ReviewResponses() {}

  public record Summary(
      Long displayId,
      String title,
      Long requesterId,
      String status,
      Instant requestedAt,
      String school,
      String leaderName,
      LocalDate startDate,
      LocalDate endDate,
      String posterImageUrl,
      @Schema(
              types = {"object", "null"},
              description = "대표 포스터 표시 범위. null이면 조정 없음.")
          PosterCropResponse posterCrop) {}

  public record Page(List<Summary> items, Long nextCursor, boolean hasNext) {}

  public record Detail(
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
      String leaderName,
      @Schema(
              description = "최신 심사 요청 상태. 취소 시 CANCELLED이며 전시 status는 DRAFT입니다.",
              types = {"string", "null"})
          String screeningStatus,
      @Schema(
              description = "심사 요청 취소자 ID. 취소되지 않았으면 null입니다.",
              types = {"integer", "null"})
          Long cancelledBy) {}
}
