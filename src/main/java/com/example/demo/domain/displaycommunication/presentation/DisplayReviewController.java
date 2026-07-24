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
import com.example.demo.domain.displaycommunication.application.result.DeletedDisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.application.result.DeletedDisplayReviewResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewLikeResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyLikeResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult;
import com.example.demo.domain.displaycommunication.presentation.docs.DisplayReviewApiDocs;
import com.example.demo.domain.displaycommunication.presentation.mapper.DisplayReviewPresentationMapper;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewReplyRequest;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewRequest;
import com.example.demo.domain.displaycommunication.presentation.response.DeletedDisplayReviewReplyResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DeletedDisplayReviewResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewLikeResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewReplyLikeResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewReplyResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
  private final DisplayReviewPresentationMapper mapper;

  @Override
  @PostMapping
  // 전시 후기 작성
  public ApiResponseBody<DisplayReviewResponse> createReview(
      @PathVariable Long displayId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreateDisplayReviewRequest request,
      HttpServletRequest httpServletRequest) {
    CreateDisplayReviewCommand command = mapper.toCommand(displayId, userId, request);

    DisplayReviewResult result = createDisplayReviewService.create(command);

    DisplayReviewResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{displayReviewId}/reply")
  // 전시 후기 답글 작성
  public ApiResponseBody<DisplayReviewReplyResponse> createReviewReply(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      @Valid @RequestBody CreateDisplayReviewReplyRequest request,
      HttpServletRequest httpServletRequest) {
    CreateDisplayReviewReplyCommand command =
        mapper.toCommand(displayId, displayReviewId, userId, request);

    DisplayReviewReplyResult result = createDisplayReviewReplyService.create(command);

    DisplayReviewReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{displayReviewId}/like")
  // 전시 후기 좋아요 등록 및 취소
  public ApiResponseBody<DisplayReviewLikeResponse> reviewLike(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      HttpServletRequest httpServletRequest) {
    DisplayReviewLikeCommand command =
        new DisplayReviewLikeCommand(displayId, displayReviewId, userId);

    DisplayReviewLikeResult result = displayReviewLikeService.toggleReviewLike(command);

    DisplayReviewLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{displayReviewId}")
  // 전시 후기 삭제
  public ApiResponseBody<DeletedDisplayReviewResponse> deleteReview(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      HttpServletRequest httpServletRequest) {
    DeleteDisplayReviewCommand command =
        new DeleteDisplayReviewCommand(displayId, displayReviewId, userId);

    DeletedDisplayReviewResult result = deleteDisplayReviewService.deleteReview(command);

    DeletedDisplayReviewResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{displayReviewId}/reply/{displayReviewReplyId}")
  // 전시 후기 답글 삭제
  public ApiResponseBody<DeletedDisplayReviewReplyResponse> deleteReviewReply(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @PathVariable Long displayReviewReplyId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      HttpServletRequest httpServletRequest) {
    DeleteDisplayReviewReplyCommand command =
        new DeleteDisplayReviewReplyCommand(
            displayId, displayReviewId, displayReviewReplyId, userId);

    DeletedDisplayReviewReplyResult result =
        deleteDisplayReviewReplyService.deleteReviewReply(command);

    DeletedDisplayReviewReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{displayReviewId}/reply/{displayReviewReplyId}/like")
  // 전시 후기 답글 좋아요 등록 및 취소
  public ApiResponseBody<DisplayReviewReplyLikeResponse> reviewReplyLike(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @PathVariable Long displayReviewReplyId,
      @RequestHeader("X-User-Id") Long userId, // 테스트용
      HttpServletRequest httpServletRequest) {
    DisplayReviewReplyLikeCommand command =
        new DisplayReviewReplyLikeCommand(displayId, displayReviewId, displayReviewReplyId, userId);

    DisplayReviewReplyLikeResult result =
        displayReviewReplyLikeService.toggleReviewReplyLike(command);

    DisplayReviewReplyLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }
}
