package com.example.demo.domain.displaycommunication.presentation;

import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewCommand;
import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewReplyCommand;
import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewReplyService;
import com.example.demo.domain.displaycommunication.application.command.CreateDisplayReviewService;
import com.example.demo.domain.displaycommunication.application.command.DisplayReviewLikeCommand;
import com.example.demo.domain.displaycommunication.application.command.DisplayReviewLikeService;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewLikeResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewReplyResult;
import com.example.demo.domain.displaycommunication.application.result.DisplayReviewResult;
import com.example.demo.domain.displaycommunication.presentation.docs.DisplayReviewApiDocs;
import com.example.demo.domain.displaycommunication.presentation.mapper.DisplayReviewPresentationMapper;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewReplyRequest;
import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewRequest;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewLikeResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewReplyResponse;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class DisplayReviewController implements DisplayReviewApiDocs {
  private final CreateDisplayReviewService createDisplayReviewService;
  private final CreateDisplayReviewReplyService createDisplayReviewReplyService;
  private final DisplayReviewLikeService toggleDisplayReviewLikeService;
  private final DisplayReviewPresentationMapper mapper;

  @Override
  @PostMapping("/api/v1/display/{displayId}/reviews")
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
  @PostMapping("/api/v1/display/{displayId}/reviews/{displayReviewId}/replies")
  // 전시 후기 답글 작성
  public ApiResponseBody<DisplayReviewReplyResponse> createReviewReply(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @RequestHeader("X-User-Id") Long userId,
      @Valid @RequestBody CreateDisplayReviewReplyRequest request,
      HttpServletRequest httpServletRequest) {
    CreateDisplayReviewReplyCommand command =
        mapper.toCommand(displayId, displayReviewId, userId, request);
    DisplayReviewReplyResult result = createDisplayReviewReplyService.create(command);
    DisplayReviewReplyResponse response = mapper.toResponse(result);
    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/api/v1/display/{displayId}/reviews/{displayReviewId}/like")
  // 전시 후기 좋아요 등록 및 취소
  public ApiResponseBody<DisplayReviewLikeResponse> toggleReviewLike(
      @PathVariable Long displayId,
      @PathVariable Long displayReviewId,
      @RequestHeader("X-User-Id") Long userId,
      HttpServletRequest httpServletRequest) {
    DisplayReviewLikeResult result =
        toggleDisplayReviewLikeService.toggle(
            new DisplayReviewLikeCommand(displayId, displayReviewId, userId));
    return ApiResponseBody.success(mapper.toResponse(result), httpServletRequest);
  }
}
