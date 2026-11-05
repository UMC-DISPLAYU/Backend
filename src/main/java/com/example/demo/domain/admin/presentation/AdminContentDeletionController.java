package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.service.AdminContentDeletionService;
import com.example.demo.domain.admin.presentation.mapper.AdminContentDeletionMapper;
import com.example.demo.domain.admin.presentation.request.DeleteContentRequest;
import com.example.demo.domain.admin.presentation.response.ContentDeletionResponse;
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
@ConditionalOnProperty(name = "admin.content-deletion.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminContentDeletionController {
  private final AdminContentDeletionService service;
  private final AdminContentDeletionMapper mapper;

  @PostMapping("/{contentType}/{contentId}/delete")
  @Operation(summary = "관리자 콘텐츠 삭제")
  public ApiResponseBody<ContentDeletionResponse> delete(
      @AuthenticationPrincipal AuthUser operator,
      @PathVariable String contentType,
      @PathVariable Long contentId,
      @RequestBody DeleteContentRequest body,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.delete(
                operator == null ? null : operator.userId(),
                contentType,
                contentId,
                body.reason())),
        request);
  }
}
