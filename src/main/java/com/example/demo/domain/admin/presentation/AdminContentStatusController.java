package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.service.AdminContentStatusService;
import com.example.demo.domain.admin.presentation.mapper.AdminContentStatusMapper;
import com.example.demo.domain.admin.presentation.request.ChangeContentStatusRequest;
import com.example.demo.domain.admin.presentation.response.ContentStatusResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/contents")
@ConditionalOnProperty(name = "admin.content-status.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminContentStatusController {
  private final AdminContentStatusService service;
  private final AdminContentStatusMapper mapper;

  @PatchMapping("/{contentType}/{contentId}/operation-status")
  @Operation(summary = "관리자 콘텐츠 중단·재개")
  public ApiResponseBody<ContentStatusResponse> changeStatus(
      @AuthenticationPrincipal AuthUser operator,
      @PathVariable String contentType,
      @PathVariable Long contentId,
      @RequestBody ChangeContentStatusRequest body,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.changeStatus(
                operator == null ? null : operator.userId(),
                contentType,
                contentId,
                body.operationStatus(),
                body.reason())),
        request);
  }
}
