package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.query.ReviewSearchQuery;
import com.example.demo.domain.admin.application.service.AdminReviewService;
import com.example.demo.domain.admin.presentation.mapper.AdminReviewMapper;
import com.example.demo.domain.admin.presentation.request.RejectReviewRequest;
import com.example.demo.domain.admin.presentation.response.ReviewResponses;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/displays")
@ConditionalOnProperty(name = "admin.review.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminReviewController {
  private final AdminReviewService service;
  private final AdminReviewMapper mapper;

  @GetMapping
  @Operation(summary = "운영자 전시 심사 목록 조회")
  public ApiResponseBody<ReviewResponses.Page> search(
      @AuthenticationPrincipal AuthUser user,
      @RequestParam(defaultValue = "PENDING_REVIEW") String status,
      @RequestParam(required = false) Long cursor,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.search(userId(user), new ReviewSearchQuery(status, cursor, size))),
        request);
  }

  @GetMapping("/{displayId}")
  @Operation(summary = "운영자 전시 심사 상세 조회")
  public ApiResponseBody<ReviewResponses.Detail> detail(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long displayId,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(service.getDetail(userId(user), displayId)), request);
  }

  @PostMapping("/{displayId}/approve")
  @Operation(summary = "운영자 전시 승인")
  public ApiResponseBody<Void> approve(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long displayId,
      HttpServletRequest request) {
    service.approve(userId(user), displayId);
    return ApiResponseBody.success(null, request);
  }

  @PostMapping("/{displayId}/reject")
  @Operation(summary = "운영자 전시 반려")
  public ApiResponseBody<Void> reject(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long displayId,
      @Valid @RequestBody RejectReviewRequest body,
      HttpServletRequest request) {
    service.reject(userId(user), displayId, body.reason());
    return ApiResponseBody.success(null, request);
  }

  private Long userId(AuthUser user) {
    return user == null ? null : user.userId();
  }
}
