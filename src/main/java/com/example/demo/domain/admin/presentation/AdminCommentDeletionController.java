package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.service.AdminCommentDeletionService;
import com.example.demo.domain.admin.presentation.mapper.AdminCommentDeletionMapper;
import com.example.demo.domain.admin.presentation.request.DeleteCommentRequest;
import com.example.demo.domain.admin.presentation.response.CommentDeletionResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/admin/comments")
@ConditionalOnProperty(name = "admin.comments.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminCommentDeletionController {
  private final AdminCommentDeletionService service;
  private final AdminCommentDeletionMapper mapper;

  @PostMapping("/{commentType}/{commentId}/delete")
  @Operation(summary = "관리자 댓글 삭제")
  public ApiResponseBody<CommentDeletionResponse> delete(
      @AuthenticationPrincipal AuthUser operator,
      @Parameter(description = "댓글 소유 도메인의 대문자 타입 코드") @PathVariable String commentType,
      @PathVariable Long commentId,
      @Valid @RequestBody DeleteCommentRequest body,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            service.delete(
                operator == null ? null : operator.userId(),
                commentType,
                commentId,
                body.reason())),
        request);
  }
}
