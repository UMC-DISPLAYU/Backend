package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.query.ReportCaseSearchQuery;
import com.example.demo.domain.admin.application.service.AdminReportCaseService;
import com.example.demo.domain.admin.presentation.mapper.AdminReportCaseMapper;
import com.example.demo.domain.admin.presentation.response.ReportCaseResponses;
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
@ConditionalOnProperty(name = "admin.report-cases.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminReportCaseController {
  private final AdminReportCaseService service;
  private final AdminReportCaseMapper mapper;

  @GetMapping
  @Operation(summary = "관리자 댓글 신고 묶음 목록 조회")
  public ApiResponseBody<ReportCaseResponses.Page> search(
      @AuthenticationPrincipal AuthUser admin,
      @RequestParam(defaultValue = "PENDING") String status,
      @RequestParam(required = false) Long cursor,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.search(adminId(admin), new ReportCaseSearchQuery(status, cursor, size))),
        request);
  }

  @GetMapping("/{reportCaseId}")
  @Operation(summary = "관리자 댓글 신고 묶음 상세 조회")
  public ApiResponseBody<ReportCaseResponses.Detail> detail(
      @AuthenticationPrincipal AuthUser admin,
      @PathVariable Long reportCaseId,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(service.getDetail(adminId(admin), reportCaseId)), request);
  }

  private Long adminId(AuthUser admin) {
    return admin == null ? null : admin.userId();
  }
}
