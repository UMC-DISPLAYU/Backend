package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.service.AdminParentDeletionService;
import com.example.demo.domain.admin.presentation.mapper.AdminParentDeletionMapper;
import com.example.demo.domain.admin.presentation.request.DeleteParentRequest;
import com.example.demo.domain.admin.presentation.response.ParentDeletionResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/parents")
@ConditionalOnProperty(name = "admin.parents.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminParentDeletionController {
  private final AdminParentDeletionService service;
  private final AdminParentDeletionMapper mapper;

  @PostMapping("/{parentType}/{parentId}/delete")
  @Operation(summary = "관리자 후기·방명록 삭제")
  public ApiResponseBody<ParentDeletionResponse> delete(
      @AuthenticationPrincipal AuthUser operator,
      @Parameter(description = "상위 자료 소유 도메인의 대문자 타입 코드") @PathVariable String parentType,
      @PathVariable Long parentId,
      @RequestBody DeleteParentRequest body,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.delete(
                operator == null ? null : operator.userId(), parentType, parentId, body.reason())),
        request);
  }
}
