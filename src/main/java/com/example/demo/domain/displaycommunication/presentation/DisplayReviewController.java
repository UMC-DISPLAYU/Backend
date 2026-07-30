package com.example.demo.domain.displaycommunication.presentation;

import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewCommand;
import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewReplyCommand;
import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewReplyService;
import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewService;
import com.example.demo.domain.displaycommunication.application.command.DeleteDisplayReviewCommand;
import com.example.demo.domain.displaycommunication.application.command.DeleteDisplayReviewReplyCommand;
import com.example.demo.domain.displaycommunication.application.command.DeleteDisplayReviewReplyService;
import com.example.demo.domain.displaycommunication.application.command.DeleteDisplayReviewService;
import com.example.demo.domain.displaycommunication.application.command.DisplayReviewLikeCommand;
import com.example.demo.domain.displaycommunication.application.command.DisplayReviewLikeService;
import com.example.demo.domain.displaycommunication.application.command.DisplayReviewReplyLikeCommand;
import com.example.demo.domain.displaycommunication.application.command.DisplayReviewReplyLikeService;
import com.example.demo.domain.displaycommunication.application.query.GetDisplayReviewRepliesService;
import com.example.demo.domain.displaycommunication.application.query.GetDisplayReviewsService;
import com.example.demo.domain.displaycommunication.application.result.DeletedDisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.application.result.DeletedDisplayReviewResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewLikeResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewListResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyLikeResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyListResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult;
import com.example.demo.domain.displaycommunication.presentation.docs.DisplayReviewApiDocs;
import com.example.demo.domain.displaycommunication.presentation.mapper.DisplayReviewPresentationMapper;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewReplyRequest;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewRequest;
import com.example.demo.domain.displaycommunication.presentation.response.DeletedDisplayReviewReplyResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DeletedDisplayReviewResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewLikeResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewListResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewReplyLikeResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewReplyListResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewReplyResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/display/{displayId}/reviews")
public class DisplayReviewController implements DisplayReviewApiDocs {
  private final CreateDisplayReviewService createDisplayReviewService;
  private final CreateDisplayReviewReplyService createDisplayReviewReplyService;
  private final DeleteDisplayReviewReplyService deleteDisplayReviewReplyService;
  private final DeleteDisplayReviewService deleteDisplayReviewService;
  private final DisplayReviewLikeService displayReviewLikeService;
  private final DisplayReviewReplyLikeService displayReviewReplyLikeService;
  private final GetDisplayReviewRepliesService getDisplayReviewRepliesService;
  private final GetDisplayReviewsService getDisplayReviewsService;
  private final DisplayReviewPresentationMapper mapper;

  @Override
  @GetMapping
  // 전시 후기 및 답글 목록 조회
  public ApiResponseBody<DisplayReviewListResponse> getReviews(
      @PathVariable Long displayId,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest httpServletRequest) {
    DisplayReviewListResult result =
        getDisplayReviewsService.getReviews(mapper.toQuery(displayId, cursorId, size));

    DisplayReviewListResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @GetMapping("/{displayReviewId}/replies")
  // 전시 후기 답글 목록 조회
  public ApiResponseBody<DisplayReviewReplyListResponse> getReviewReplies(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest httpServletRequest) {
    DisplayReviewReplyListResult result =
        getDisplayReviewRepliesService.getReplies(
            mapper.toReplyQuery(displayId, displayReviewId, cursorId, size));

    DisplayReviewReplyListResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping
  @SecurityRequirement(name = "Authorization")
  // 전시 후기 작성
  public ApiResponseBody<DisplayReviewResponse> createReview(
      @PathVariable Long displayId,
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody CreateDisplayReviewRequest request,
      HttpServletRequest httpServletRequest) {
    CreateDisplayReviewCommand command = mapper.toCommand(displayId, requireUserId(user), request);

    DisplayReviewResult result = createDisplayReviewService.create(command);

    DisplayReviewResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{displayReviewId}/replies")
  @SecurityRequirement(name = "Authorization")
  // 전시 후기 답글 작성
  public ApiResponseBody<DisplayReviewReplyResponse> createReviewReply(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody CreateDisplayReviewReplyRequest request,
      HttpServletRequest httpServletRequest) {
    CreateDisplayReviewReplyCommand command =
        mapper.toCommand(displayId, displayReviewId, requireUserId(user), request);

    DisplayReviewReplyResult result = createDisplayReviewReplyService.create(command);

    DisplayReviewReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{displayReviewId}/like")
  @SecurityRequirement(name = "Authorization")
  // 전시 후기 좋아요 등록 및 취소
  public ApiResponseBody<DisplayReviewLikeResponse> reviewLike(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    DisplayReviewLikeCommand command =
        new DisplayReviewLikeCommand(displayId, displayReviewId, requireUserId(user));

    DisplayReviewLikeResult result = displayReviewLikeService.toggleReviewLike(command);

    DisplayReviewLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{displayReviewId}")
  @SecurityRequirement(name = "Authorization")
  // 전시 후기 삭제
  public ApiResponseBody<DeletedDisplayReviewResponse> deleteReview(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    DeleteDisplayReviewCommand command =
        new DeleteDisplayReviewCommand(displayId, displayReviewId, requireUserId(user));

    DeletedDisplayReviewResult result = deleteDisplayReviewService.deleteReview(command);

    DeletedDisplayReviewResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{displayReviewId}/reply/{displayReviewReplyId}")
  @SecurityRequirement(name = "Authorization")
  // 전시 후기 답글 삭제
  public ApiResponseBody<DeletedDisplayReviewReplyResponse> deleteReviewReply(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @PathVariable Long displayReviewReplyId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    DeleteDisplayReviewReplyCommand command =
        new DeleteDisplayReviewReplyCommand(
            displayId, displayReviewId, displayReviewReplyId, requireUserId(user));

    DeletedDisplayReviewReplyResult result =
        deleteDisplayReviewReplyService.deleteReviewReply(command);

    DeletedDisplayReviewReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{displayReviewId}/reply/{displayReviewReplyId}/like")
  @SecurityRequirement(name = "Authorization")
  // 전시 후기 답글 좋아요 등록 및 취소
  public ApiResponseBody<DisplayReviewReplyLikeResponse> reviewReplyLike(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @PathVariable Long displayReviewReplyId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    DisplayReviewReplyLikeCommand command =
        new DisplayReviewReplyLikeCommand(
            displayId, displayReviewId, displayReviewReplyId, requireUserId(user));

    DisplayReviewReplyLikeResult result =
        displayReviewReplyLikeService.toggleReviewReplyLike(command);

    DisplayReviewReplyLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
