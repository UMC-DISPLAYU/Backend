package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.service.AdminParentService;
import com.example.demo.domain.admin.presentation.mapper.AdminParentMapper;
import com.example.demo.domain.admin.presentation.response.ParentResponses;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/parents")
@ConditionalOnProperty(name = "admin.parents.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminParentController {
  private final AdminParentService service;
  private final AdminParentMapper mapper;

  @GetMapping("/{parentType}/{parentId}")
  @Operation(summary = "관리자 상위 후기·방명록 상세 조회")
  public ApiResponseBody<ParentResponses.Detail> detail(
      @AuthenticationPrincipal AuthUser admin,
      @Parameter(description = "상위 자료 소유 도메인의 대문자 타입 코드") @PathVariable String parentType,
      @PathVariable Long parentId,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(service.getDetail(adminId(admin), parentType, parentId)), request);
  }

  private Long adminId(AuthUser admin) {
    return admin == null ? null : admin.userId();
  }
}
