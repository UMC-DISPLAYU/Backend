package com.example.demo.domain.report.presentation.docs;

import com.example.demo.domain.report.presentation.request.CreateCommentReportRequest;
import com.example.demo.domain.report.presentation.request.CreateContentReportRequest;
import com.example.demo.domain.report.presentation.response.CreateCommentReportResponse;
import com.example.demo.domain.report.presentation.response.CreateContentReportResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@Tag(name = "콘텐츠 신고", description = "전시 후기와 작품 방명록 신고 접수")
@SecurityRequirement(name = "Authorization")
@ApiResponses({
  @ApiResponse(responseCode = "400", description = "신고 사유 누락 또는 잘못된 사유/ID"),
  @ApiResponse(responseCode = "401", description = "로그인 필요"),
  @ApiResponse(
      responseCode = "403",
      description = "SELF_REPORT_NOT_ALLOWED: 본인이 작성한 댓글은 신고할 수 없습니다."),
  @ApiResponse(responseCode = "404", description = "부모 콘텐츠 또는 신고 대상이 없거나 삭제됨, 대상이 부모에 속하지 않음"),
  @ApiResponse(responseCode = "409", description = "CONTENT_REPORT_ALREADY_EXISTS: 이미 신고한 댓글입니다.")
})
public interface ContentReportApiDocs {
  @Operation(
      summary = "전시 후기 신고 생성",
      description = "로그인한 사용자가 전시 후기를 신고합니다. 동일 사용자는 같은 후기를 사유에 관계없이 한 번만 신고할 수 있습니다.")
  @ApiResponse(responseCode = "200", description = "신고 접수 성공", useReturnTypeSchema = true)
  ApiResponseBody<CreateContentReportResponse> reportReview(
      @Positive Long displayId,
      @Positive Long displayReviewId,
      @Parameter(hidden = true) AuthUser user,
      @Valid CreateContentReportRequest request,
      @Parameter(hidden = true) HttpServletRequest servletRequest);

  @Operation(
      summary = "작품 방명록 신고 생성",
      description = "로그인한 사용자가 작품 방명록을 신고합니다. 신고자와 작성자 ID는 서버에서 결정합니다.")
  @ApiResponse(responseCode = "200", description = "신고 접수 성공", useReturnTypeSchema = true)
  ApiResponseBody<CreateContentReportResponse> reportFeeling(
      @Positive Long artworkId,
      @Positive Long feelingId,
      @Parameter(hidden = true) AuthUser user,
      @Valid CreateContentReportRequest request,
      @Parameter(hidden = true) HttpServletRequest servletRequest);

  @Operation(
      summary = "댓글 신고 접수",
      description =
          "로그인 사용자가 후기·방명록 답글을 신고합니다. 지원 타입: DISPLAY_REVIEW_REPLY, ARTWORK_FEELING_REPLY, PERSONAL_ARTWORK_FEELING_REPLY")
  @ApiResponse(responseCode = "200", description = "신고 접수 성공", useReturnTypeSchema = true)
  ApiResponseBody<CreateCommentReportResponse> reportComment(
      String commentType,
      @Positive Long commentId,
      @Parameter(hidden = true) AuthUser user,
      @Valid CreateCommentReportRequest request,
      @Parameter(hidden = true) HttpServletRequest servletRequest);
}
