package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.query.OperationLogSearchQuery;
import com.example.demo.domain.admin.application.service.AdminOperationLogService;
import com.example.demo.domain.admin.presentation.mapper.AdminOperationLogMapper;
import com.example.demo.domain.admin.presentation.response.OperationLogResponses;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/operations")
@ConditionalOnProperty(name = "admin.operations.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminOperationLogController {
  private final AdminOperationLogService service;
  private final AdminOperationLogMapper mapper;

  @GetMapping
  @Operation(summary = "관리자 운영 이력 목록·검색")
  public ApiResponseBody<OperationLogResponses.Page> search(
      @AuthenticationPrincipal AuthUser admin,
      @Parameter(description = "사유 부분 일치. 앞뒤 공백 제거 후 100자까지") @RequestParam(required = false)
          String q,
      @Parameter(description = "처리 시각 시작(포함), ISO-8601")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant from,
      @Parameter(description = "처리 시각 끝(제외), ISO-8601")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant to,
      @Parameter(description = "대상 종류 코드. targetId와 함께 쓰면 대상 1건의 이력")
          @RequestParam(required = false)
          String targetType,
      @RequestParam(required = false) Long targetId,
      @Parameter(description = "조치 코드(예: DELETE)") @RequestParam(required = false) String action,
      @RequestParam(required = false) Long operatorId,
      @RequestParam(required = false) Long cursor,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.search(
                admin == null ? null : admin.userId(),
                new OperationLogSearchQuery(
                    q, from, to, targetType, targetId, action, operatorId, cursor, size))),
        request);
  }
}
