package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.query.UserSearchQuery;
import com.example.demo.domain.admin.application.service.AdminUserService;
import com.example.demo.domain.admin.presentation.mapper.AdminUserMapper;
import com.example.demo.domain.admin.presentation.response.UserResponses;
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
@RequestMapping("/api/v1/admin/users")
@ConditionalOnProperty(name = "admin.users.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminUserController {
  private final AdminUserService service;
  private final AdminUserMapper mapper;

  @GetMapping
  @Operation(summary = "관리자 회원 목록 조회")
  public ApiResponseBody<UserResponses.Page> search(
      @AuthenticationPrincipal AuthUser admin,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String accountStatus,
      @RequestParam(required = false) Long cursor,
      @RequestParam(defaultValue = "20") int size,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.search(adminId(admin), new UserSearchQuery(q, accountStatus, cursor, size))),
        request);
  }

  @GetMapping("/{userId}")
  @Operation(summary = "관리자 회원 상세 조회")
  public ApiResponseBody<UserResponses.Detail> detail(
      @AuthenticationPrincipal AuthUser admin,
      @PathVariable Long userId,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(service.getDetail(adminId(admin), userId)), request);
  }

  private Long adminId(AuthUser admin) {
    return admin == null ? null : admin.userId();
  }
}
