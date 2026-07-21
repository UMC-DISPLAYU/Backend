package com.example.demo.domain.lounge.presentation;

import com.example.demo.domain.lounge.application.command.LoungePostCommandService;
import com.example.demo.domain.lounge.application.query.LoungePostQueryService;
import com.example.demo.domain.lounge.application.result.LoungePostDetailResult;
import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.presentation.docs.LoungePostControllerDocs;
import com.example.demo.domain.lounge.presentation.mapper.LoungePresentationMapper;
import com.example.demo.domain.lounge.presentation.request.LoungePostRequest;
import com.example.demo.domain.lounge.presentation.response.LoungePostCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostDetailResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostScrapResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
public class LoungePostController implements LoungePostControllerDocs {

  private final LoungePostCommandService loungePostCommandService;
  private final LoungePostQueryService loungePostQueryService;
  private final LoungePresentationMapper mapper;

  public LoungePostController(
      LoungePostCommandService loungePostCommandService,
      LoungePostQueryService loungePostQueryService,
      LoungePresentationMapper mapper) {
    this.loungePostCommandService = loungePostCommandService;
    this.loungePostQueryService = loungePostQueryService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/lounge/posts")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ApiResponseBody<LoungePostDetailResponse> createPost(
      @RequestBody LoungePostRequest loungePostRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    Long loungePostId =
        loungePostCommandService.createPost(user.userId(), loungePostRequest.toCommand());
    LoungePostDetailResult result =
        loungePostQueryService.getPostDetail(loungePostId, user.userId());
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PatchMapping("/api/v1/lounge/posts/{loungePostId}")
  @Override
  public ApiResponseBody<LoungePostDetailResponse> updatePost(
      @PathVariable Long loungePostId,
      @RequestBody LoungePostRequest loungePostRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    loungePostCommandService.updatePost(loungePostId, user.userId(), loungePostRequest.toCommand());
    return ApiResponseBody.success(
        mapper.toResponse(loungePostQueryService.getPostDetail(loungePostId, user.userId())),
        request);
  }

  @DeleteMapping("/api/v1/lounge/posts/{loungePostId}")
  @Override
  public ApiResponseBody<Void> deletePost(
      @PathVariable Long loungePostId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    loungePostCommandService.deletePost(loungePostId, user.userId());
    return ApiResponseBody.success(null, request);
  }

  @PostMapping("/api/v1/lounge/posts/{loungePostId}/likes")
  @Override
  public ApiResponseBody<LoungePostLikeResponse> likePost(
      @PathVariable Long loungePostId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(loungePostCommandService.likePost(loungePostId, user.userId())), request);
  }

  @DeleteMapping("/api/v1/lounge/posts/{loungePostId}/likes")
  @Override
  public ApiResponseBody<LoungePostLikeResponse> cancelLikePost(
      @PathVariable Long loungePostId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(loungePostCommandService.cancelLikePost(loungePostId, user.userId())),
        request);
  }

  @PostMapping("/api/v1/lounge/posts/{loungePostId}/scraps")
  @Override
  public ApiResponseBody<LoungePostScrapResponse> scrapPost(
      @PathVariable Long loungePostId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(loungePostCommandService.scrapPost(loungePostId, user.userId())),
        request);
  }

  @DeleteMapping("/api/v1/lounge/posts/{loungePostId}/scraps")
  @Override
  public ApiResponseBody<LoungePostScrapResponse> cancelScrapPost(
      @PathVariable Long loungePostId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(loungePostCommandService.cancelScrapPost(loungePostId, user.userId())),
        request);
  }

  @GetMapping("/api/v1/lounge/posts")
  @Override
  public ApiResponseBody<LoungePostCursorResponse> getPosts(
      @RequestParam(required = false) LoungePostCategory category,
      @RequestParam(required = false) Long cursorId,
      @RequestParam(defaultValue = "10") int size,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    Long viewerUserId = user == null ? null : user.userId();
    return ApiResponseBody.success(
        mapper.toResponse(
            loungePostQueryService.getPosts(category, cursorId, size, viewerUserId)),
        request);
  }

  @GetMapping("/api/v1/lounge/posts/{loungePostId}")
  @Override
  public ApiResponseBody<LoungePostDetailResponse> getPostDetail(
      @PathVariable Long loungePostId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    Long viewerUserId = user == null ? null : user.userId();
    return ApiResponseBody.success(
        mapper.toResponse(loungePostQueryService.getPostDetail(loungePostId, viewerUserId)),
        request);
  }
}
