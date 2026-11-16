package com.example.demo.domain.admin.presentation;

import com.example.demo.domain.admin.application.service.AdminCommentService;
import com.example.demo.domain.admin.presentation.mapper.AdminCommentMapper;
import com.example.demo.domain.admin.presentation.response.CommentResponses;
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
@RequestMapping("/api/v1/admin/comments")
@ConditionalOnProperty(name = "admin.comments.enabled", havingValue = "true")
@SecurityRequirement(name = "Authorization")
public class AdminCommentController {
  private final AdminCommentService service;
  private final AdminCommentMapper mapper;

  @GetMapping("/{commentType}/{commentId}")
  @Operation(summary = "관리자 댓글 상세 조회")
  public ApiResponseBody<CommentResponses.Detail> detail(
      @AuthenticationPrincipal AuthUser admin,
      @Parameter(description = "댓글 소유 도메인의 대문자 타입 코드") @PathVariable String commentType,
      @PathVariable Long commentId,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(service.getDetail(adminId(admin), commentType, commentId)), request);
  }

  private Long adminId(AuthUser admin) {
    return admin == null ? null : admin.userId();
  }
}
