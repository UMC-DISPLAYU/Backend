package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.service.AdminReportResolutionService;
import com.example.demo.domain.admin.presentation.mapper.AdminReportResolutionMapper;
import com.example.demo.domain.admin.presentation.request.ResolveReportCaseRequest;
import com.example.demo.domain.admin.presentation.response.ReportResolutionResponse;
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
@RequestMapping("/api/v1/admin/report-cases")
@ConditionalOnProperty(name = "admin.report-resolution.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminReportResolutionController {
  private final AdminReportResolutionService service;
  private final AdminReportResolutionMapper mapper;

  @PostMapping("/{reportCaseId}/resolve")
  @Operation(summary = "관리자 댓글 신고 처리")
  public ApiResponseBody<ReportResolutionResponse> resolve(
      @AuthenticationPrincipal AuthUser operator,
      @PathVariable Long reportCaseId,
      @RequestBody ResolveReportCaseRequest body,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.resolve(
                operator == null ? null : operator.userId(),
                body.action(),
                reportCaseId,
                body.reason())),
        request);
  }
}
