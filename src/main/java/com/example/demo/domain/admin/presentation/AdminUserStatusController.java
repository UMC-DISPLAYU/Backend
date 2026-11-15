package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.service.AdminUserStatusService;
import com.example.demo.domain.admin.presentation.docs.AdminUserStatusControllerDocs;
import com.example.demo.domain.admin.presentation.mapper.AdminUserStatusMapper;
import com.example.demo.domain.admin.presentation.request.ChangeUserStatusRequest;
import com.example.demo.domain.admin.presentation.response.UserAccountStatusResponse;
import com.example.demo.domain.admin.presentation.response.UserStatusResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/users")
@ConditionalOnProperty(name = "admin.user-status.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminUserStatusController implements AdminUserStatusControllerDocs {
  private final AdminUserStatusService service;
  private final AdminUserStatusMapper mapper;

  @Override
  @GetMapping("/{userId}/account-status")
  public ApiResponseBody<UserAccountStatusResponse> getStatus(
      @AuthenticationPrincipal AuthUser operator,
      @PathVariable Long userId,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(service.getStatus(operator == null ? null : operator.userId(), userId)),
        request);
  }

  @Override
  @PatchMapping("/{userId}/account-status")
  public ApiResponseBody<UserStatusResponse> changeStatus(
      @AuthenticationPrincipal AuthUser operator,
      @PathVariable Long userId,
      @RequestBody ChangeUserStatusRequest body,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.changeStatus(
                operator == null ? null : operator.userId(),
                userId,
                body.accountStatus(),
                body.reason())),
        request);
  }
}
