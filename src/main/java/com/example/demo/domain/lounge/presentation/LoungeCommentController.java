package com.example.demo.domain.lounge.presentation;

import com.example.demo.domain.lounge.application.command.LoungeCommentCommandService;
import com.example.demo.domain.lounge.application.query.LoungeCommentQueryService;
import com.example.demo.domain.lounge.presentation.docs.LoungeCommentControllerDocs;
import com.example.demo.domain.lounge.presentation.mapper.LoungePresentationMapper;
import com.example.demo.domain.lounge.presentation.request.LoungeCommentRequest;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentListResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeMyCommentCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeReplyCursorResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class LoungeCommentController implements LoungeCommentControllerDocs {

  private final LoungeCommentCommandService loungeCommentCommandService;
  private final LoungeCommentQueryService loungeCommentQueryService;
  private final LoungePresentationMapper mapper;

  public LoungeCommentController(
      LoungeCommentCommandService loungeCommentCommandService,
      LoungeCommentQueryService loungeCommentQueryService,
      LoungePresentationMapper mapper) {
    this.loungeCommentCommandService = loungeCommentCommandService;
    this.loungeCommentQueryService = loungeCommentQueryService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/lounge/posts/{loungePostId}/comments")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ApiResponseBody<LoungeCommentListResponse> createComment(
      @PathVariable Long loungePostId,
      @RequestBody LoungeCommentRequest loungeCommentRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    Long loungeCommentId =
        loungeCommentCommandService.createComment(
            loungePostId, user.userId(), loungeCommentRequest.toCommand());
    return ApiResponseBody.success(
        mapper.toResponse(loungeCommentQueryService.getComment(loungeCommentId, user.userId())),
        request);
  }

  @PostMapping("/api/v1/lounge/comments/{parentCommentId}/replies")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ApiResponseBody<LoungeCommentListResponse> createReply(
      @PathVariable Long parentCommentId,
      @RequestBody LoungeCommentRequest loungeCommentRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    Long loungeCommentId =
        loungeCommentCommandService.createReply(
            parentCommentId, user.userId(), loungeCommentRequest.toCommand());
    return ApiResponseBody.success(
        mapper.toResponse(loungeCommentQueryService.getComment(loungeCommentId, user.userId())),
        request);
  }

  @DeleteMapping("/api/v1/lounge/comments/{loungeCommentId}")
  @Override
  public ApiResponseBody<Void> deleteComment(
      @PathVariable Long loungeCommentId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    loungeCommentCommandService.deleteComment(loungeCommentId, user.userId());
    return ApiResponseBody.success(null, request);
  }

  @PostMapping("/api/v1/lounge/comments/{loungeCommentId}/likes")
  @Override
  public ApiResponseBody<LoungeCommentLikeResponse> likeComment(
      @PathVariable Long loungeCommentId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(loungeCommentCommandService.likeComment(loungeCommentId, user.userId())),
        request);
  }

  @DeleteMapping("/api/v1/lounge/comments/{loungeCommentId}/likes")
  @Override
  public ApiResponseBody<LoungeCommentLikeResponse> cancelLikeComment(
      @PathVariable Long loungeCommentId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            loungeCommentCommandService.cancelLikeComment(loungeCommentId, user.userId())),
        request);
  }

  @GetMapping("/api/v1/lounge/posts/{loungePostId}/comments")
  @Override
  public ApiResponseBody<LoungeCommentCursorResponse> getComments(
      @PathVariable Long loungePostId,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    Long viewerUserId = user == null ? null : user.userId();
    return ApiResponseBody.success(
        mapper.toResponse(
            loungeCommentQueryService.getComments(loungePostId, cursorId, size, viewerUserId)),
        request);
  }

  @GetMapping("/api/v1/lounge/comments/{parentCommentId}/replies")
  @Override
  public ApiResponseBody<LoungeReplyCursorResponse> getReplies(
      @PathVariable Long parentCommentId,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    Long viewerUserId = user == null ? null : user.userId();
    return ApiResponseBody.success(
        mapper.toResponse(
            loungeCommentQueryService.getReplies(parentCommentId, cursorId, size, viewerUserId)),
        request);
  }

  @GetMapping("/api/v1/lounge/me/comments")
  @Override
  public ApiResponseBody<LoungeMyCommentCursorResponse> getMyComments(
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toMyCommentResponse(
            loungeCommentQueryService.getMyComments(requireUserId(user), cursorId, size)),
        request);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
