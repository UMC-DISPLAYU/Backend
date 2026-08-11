package com.example.demo.domain.artworkcommunication.presentation;

import com.example.demo.domain.artworkcommunication.application.command.*;
import com.example.demo.domain.artworkcommunication.application.query.GetArtworkFeelingRepliesService;
import com.example.demo.domain.artworkcommunication.application.query.GetArtworkFeelingsService;
import com.example.demo.domain.artworkcommunication.application.result.*;
import com.example.demo.domain.artworkcommunication.presentation.docs.ArtworkFeelingApiDocs;
import com.example.demo.domain.artworkcommunication.presentation.mapper.ArtworkFeelingPresentationMapper;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.*;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/artworks/{artworkId}/feelings")
public class ArtworkFeelingController implements ArtworkFeelingApiDocs {

  private final CreateArtworkFeelingService createArtworkFeelingService;
  private final DeleteArtworkFeelingService deleteArtworkFeelingService;
  private final CreateArtworkFeelingReplyService createArtworkFeelingReplyService;
  private final DeleteArtworkFeelingReplyService deleteArtworkFeelingReplyService;
  private final GetArtworkFeelingsService getArtworkFeelingsService;
  private final GetArtworkFeelingRepliesService getArtworkFeelingRepliesService;
  private final ArtworkFeelingLikeService artworkFeelingLikeService;
  private final ArtworkFeelingReplyLikeService artworkFeelingReplyLikeService;
  private final ArtworkFeelingPresentationMapper mapper;

  @Override
  @GetMapping
  // 감상평 목록 및 답변 조회
  public ApiResponseBody<ArtworkFeelingListResponse> getFeelings(
      @PathVariable Long artworkId,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingListResult result =
        getArtworkFeelingsService.getFeelings(
            mapper.toQuery(artworkId, cursorId, size, optionalUserId(user)));

    ArtworkFeelingListResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @GetMapping("/{feelingId}/replies")
  // 감상평 답변 목록 조회
  public ApiResponseBody<ArtworkFeelingReplyListResponse> getFeelingReplies(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingReplyListResult result =
        getArtworkFeelingRepliesService.getReplies(
            mapper.toRepliesQuery(artworkId, feelingId, cursorId, size, optionalUserId(user)));

    ArtworkFeelingReplyListResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping
  @SecurityRequirement(name = "Authorization")
  // 감상평 작성
  public ApiResponseBody<ArtworkFeelingResponse> createFeeling(
      @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody CreateArtworkFeelingRequest request,
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingCommand command = mapper.toCommand(artworkId, requireUserId(user), request);

    ArtworkFeelingResult result = createArtworkFeelingService.createFeeling(command);

    ArtworkFeelingResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{feelingId}/replies")
  @SecurityRequirement(name = "Authorization")
  // 감상평 답변 등록
  public ApiResponseBody<ArtworkFeelingReplyResponse> createFeelingReply(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody CreateArtworkFeelingReplyRequest request,
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingReplyCommand command =
        mapper.toCommand(artworkId, feelingId, requireUserId(user), request);

    ArtworkFeelingReplyResult result = createArtworkFeelingReplyService.createFeelingReply(command);

    ArtworkFeelingReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{feelingId}/replies/{feelingReplyId}")
  @SecurityRequirement(name = "Authorization")
  // 감상평 답변 삭제
  public ApiResponseBody<DeletedArtworkFeelingReplyResponse> deleteFeelingReply(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @PathVariable Long feelingReplyId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    DeleteArtworkFeelingReplyCommand command =
        new DeleteArtworkFeelingReplyCommand(
            artworkId, feelingId, feelingReplyId, requireUserId(user));

    DeletedArtworkFeelingReplyResult result = deleteArtworkFeelingReplyService.deleteReply(command);

    DeletedArtworkFeelingReplyResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{feelingId}")
  @SecurityRequirement(name = "Authorization")
  // 감상평 삭제
  public ApiResponseBody<DeletedArtworkFeelingResponse> deleteFeeling(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    DeleteArtworkFeelingCommand command =
        new DeleteArtworkFeelingCommand(artworkId, feelingId, requireUserId(user));

    DeletedArtworkFeelingResult result = deleteArtworkFeelingService.deleteFeeling(command);

    DeletedArtworkFeelingResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{feelingId}/likes")
  @SecurityRequirement(name = "Authorization")
  // 감상평 좋아요 등록
  public ApiResponseBody<ArtworkFeelingLikeResponse> feelingLike(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingLikeCommand command =
        new ArtworkFeelingLikeCommand(artworkId, feelingId, requireUserId(user));

    ArtworkFeelingLikeResult result = artworkFeelingLikeService.like(command);

    ArtworkFeelingLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{feelingId}/likes")
  @SecurityRequirement(name = "Authorization")
  // 감상평 좋아요 취소
  public ApiResponseBody<ArtworkFeelingLikeResponse> cancelFeelingLike(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingLikeCommand command =
        new ArtworkFeelingLikeCommand(artworkId, feelingId, requireUserId(user));

    ArtworkFeelingLikeResult result = artworkFeelingLikeService.cancel(command);

    ArtworkFeelingLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @PostMapping("/{feelingId}/replies/{feelingReplyId}/likes")
  @SecurityRequirement(name = "Authorization")
  // 감상평 답변 좋아요 등록
  public ApiResponseBody<ArtworkFeelingReplyLikeResponse> feelingReplyLike(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @PathVariable Long feelingReplyId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingReplyLikeCommand command =
        new ArtworkFeelingReplyLikeCommand(
            artworkId, feelingId, feelingReplyId, requireUserId(user));

    ArtworkFeelingReplyLikeResult result = artworkFeelingReplyLikeService.like(command);

    ArtworkFeelingReplyLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  @Override
  @DeleteMapping("/{feelingId}/replies/{feelingReplyId}/likes")
  @SecurityRequirement(name = "Authorization")
  // 감상평 답변 좋아요 취소
  public ApiResponseBody<ArtworkFeelingReplyLikeResponse> cancelFeelingReplyLike(
      @PathVariable Long artworkId,
      @PathVariable Long feelingId,
      @PathVariable Long feelingReplyId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpServletRequest) {
    ArtworkFeelingReplyLikeCommand command =
        new ArtworkFeelingReplyLikeCommand(
            artworkId, feelingId, feelingReplyId, requireUserId(user));

    ArtworkFeelingReplyLikeResult result = artworkFeelingReplyLikeService.cancel(command);

    ArtworkFeelingReplyLikeResponse response = mapper.toResponse(result);

    return ApiResponseBody.success(response, httpServletRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }

  private Long optionalUserId(AuthUser user) {
    return user == null ? null : user.userId();
  }
}
