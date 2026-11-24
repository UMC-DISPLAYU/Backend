package com.example.demo.domain.report.presentation;

import com.example.demo.domain.report.application.command.CreateContentReportService;
import com.example.demo.domain.report.presentation.docs.ContentReportApiDocs;
import com.example.demo.domain.report.presentation.mapper.ContentReportPresentationMapper;
import com.example.demo.domain.report.presentation.request.CreateCommentReportRequest;
import com.example.demo.domain.report.presentation.request.CreateContentReportRequest;
import com.example.demo.domain.report.presentation.response.CreateCommentReportResponse;
import com.example.demo.domain.report.presentation.response.CreateContentReportResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
public class ContentReportController implements ContentReportApiDocs {
  private final CreateContentReportService service;
  private final ContentReportPresentationMapper mapper;

  @Override
  @PostMapping("/api/v1/display/{displayId}/reviews/{displayReviewId}/reports")
  public ApiResponseBody<CreateContentReportResponse> reportReview(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @AuthenticationPrincipal AuthUser user,
      @RequestBody CreateContentReportRequest request,
      HttpServletRequest servletRequest) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.reportReview(
                mapper.toReviewCommand(displayId, displayReviewId, requireUserId(user), request))),
        servletRequest);
  }

  @Override
  @PostMapping("/api/v1/artworks/{artworkId}/feelings/{feelingId}/reports")
  public ApiResponseBody<CreateContentReportResponse> reportFeeling(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @AuthenticationPrincipal AuthUser user,
      @RequestBody CreateContentReportRequest request,
      HttpServletRequest servletRequest) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.reportFeeling(
                mapper.toFeelingCommand(artworkId, feelingId, requireUserId(user), request))),
        servletRequest);
  }

  @Override
  @PostMapping("/api/v1/comments/{commentType}/{commentId}/reports")
  public ApiResponseBody<CreateCommentReportResponse> reportComment(
      @PathVariable String commentType,
      @PathVariable Long commentId,
      @AuthenticationPrincipal AuthUser user,
      @RequestBody CreateCommentReportRequest request,
      HttpServletRequest servletRequest) {
    return ApiResponseBody.success(
        mapper.toCommentResponse(
            service.reportComment(
                mapper.toCommentCommand(commentType, commentId, requireUserId(user), request))),
        servletRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null || user.userId() == null)
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    return user.userId();
  }
}
