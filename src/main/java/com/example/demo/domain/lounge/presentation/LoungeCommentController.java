package com.example.demo.domain.lounge.presentation;

import com.example.demo.domain.lounge.application.command.LoungeCommentCommandService;
import com.example.demo.domain.lounge.application.query.LoungeCommentQueryService;
import com.example.demo.domain.lounge.presentation.docs.LoungeCommentControllerDocs;
import com.example.demo.domain.lounge.presentation.mapper.LoungePresentationMapper;
import com.example.demo.domain.lounge.presentation.request.LoungeCommentRequest;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentListResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeReplyCursorResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class LoungeCommentController implements LoungeCommentControllerDocs {

  private static final Long TEMP_USER_ID = 1L;

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
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            loungeCommentCommandService.createComment(
                loungePostId, TEMP_USER_ID, loungeCommentRequest.toCommand())),
        request);
  }

  @PostMapping("/api/v1/lounge/comments/{parentCommentId}/replies")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ApiResponseBody<LoungeCommentListResponse> createReply(
      @PathVariable Long parentCommentId,
      @RequestBody LoungeCommentRequest loungeCommentRequest,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            loungeCommentCommandService.createReply(
                parentCommentId, TEMP_USER_ID, loungeCommentRequest.toCommand())),
        request);
  }

  @PatchMapping("/api/v1/lounge/comments/{loungeCommentId}")
  @Override
  public ApiResponseBody<LoungeCommentListResponse> updateComment(
      @PathVariable Long loungeCommentId,
      @RequestBody LoungeCommentRequest loungeCommentRequest,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            loungeCommentCommandService.updateComment(
                loungeCommentId, TEMP_USER_ID, loungeCommentRequest.toCommand())),
        request);
  }

  @DeleteMapping("/api/v1/lounge/comments/{loungeCommentId}")
  @Override
  public ApiResponseBody<Void> deleteComment(
      @PathVariable Long loungeCommentId, HttpServletRequest request) {
    loungeCommentCommandService.deleteComment(loungeCommentId, TEMP_USER_ID);
    return ApiResponseBody.success(null, request);
  }

  @PostMapping("/api/v1/lounge/comments/{loungeCommentId}/likes")
  @Override
  public ApiResponseBody<LoungeCommentLikeResponse> likeComment(
      @PathVariable Long loungeCommentId, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(loungeCommentCommandService.likeComment(loungeCommentId, TEMP_USER_ID)),
        request);
  }

  @DeleteMapping("/api/v1/lounge/comments/{loungeCommentId}/likes")
  @Override
  public ApiResponseBody<LoungeCommentLikeResponse> cancelLikeComment(
      @PathVariable Long loungeCommentId, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            loungeCommentCommandService.cancelLikeComment(loungeCommentId, TEMP_USER_ID)),
        request);
  }

  @GetMapping("/api/v1/lounge/posts/{loungePostId}/comments")
  @Override
  public ApiResponseBody<LoungeCommentCursorResponse> getComments(
      @PathVariable Long loungePostId,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            loungeCommentQueryService.getComments(loungePostId, cursorId, size, TEMP_USER_ID)),
        request);
  }

  @GetMapping("/api/v1/lounge/comments/{parentCommentId}/replies")
  @Override
  public ApiResponseBody<LoungeReplyCursorResponse> getReplies(
      @PathVariable Long parentCommentId,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            loungeCommentQueryService.getReplies(parentCommentId, cursorId, size, TEMP_USER_ID)),
        request);
  }
}
