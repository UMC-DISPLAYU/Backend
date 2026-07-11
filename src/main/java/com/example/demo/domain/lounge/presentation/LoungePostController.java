package com.example.demo.domain.lounge.presentation;

import com.example.demo.domain.lounge.application.command.LoungePostCommandService;
import com.example.demo.domain.lounge.application.query.LoungePostQueryService;
import com.example.demo.domain.lounge.application.result.LoungePostDetailResult;
import com.example.demo.domain.lounge.presentation.docs.LoungePostControllerDocs;
import com.example.demo.domain.lounge.presentation.mapper.LoungePresentationMapper;
import com.example.demo.domain.lounge.presentation.request.LoungePostRequest;
import com.example.demo.domain.lounge.presentation.response.LoungePostDetailResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostListResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostScrapResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoungePostController implements LoungePostControllerDocs {

  private static final Long TEMP_USER_ID = 1L;

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
      @Valid @RequestBody LoungePostRequest loungePostRequest, HttpServletRequest request) {
    Long loungePostId =
        loungePostCommandService.createPost(TEMP_USER_ID, loungePostRequest.toCommand());
    LoungePostDetailResult result = loungePostQueryService.getPostDetail(loungePostId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PatchMapping("/api/v1/lounge/posts/{loungePostId}")
  @Override
  public ApiResponseBody<LoungePostDetailResponse> updatePost(
      @PathVariable Long loungePostId,
      @Valid @RequestBody LoungePostRequest loungePostRequest,
      HttpServletRequest request) {
    loungePostCommandService.updatePost(loungePostId, TEMP_USER_ID, loungePostRequest.toCommand());
    return ApiResponseBody.success(
        mapper.toResponse(loungePostQueryService.getPostDetail(loungePostId)), request);
  }

  @DeleteMapping("/api/v1/lounge/posts/{loungePostId}")
  @Override
  public ApiResponseBody<Void> deletePost(
      @PathVariable Long loungePostId, HttpServletRequest request) {
    loungePostCommandService.deletePost(loungePostId, TEMP_USER_ID);
    return ApiResponseBody.success(null, request);
  }

  @PostMapping("/api/v1/lounge/posts/{loungePostId}/likes")
  @Override
  public ApiResponseBody<LoungePostLikeResponse> likePost(
      @PathVariable Long loungePostId, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(loungePostCommandService.likePost(loungePostId, TEMP_USER_ID)), request);
  }

  @DeleteMapping("/api/v1/lounge/posts/{loungePostId}/likes")
  @Override
  public ApiResponseBody<LoungePostLikeResponse> cancelLikePost(
      @PathVariable Long loungePostId, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(loungePostCommandService.cancelLikePost(loungePostId, TEMP_USER_ID)),
        request);
  }

  @PostMapping("/api/v1/lounge/posts/{loungePostId}/scraps")
  @Override
  public ApiResponseBody<LoungePostScrapResponse> scrapPost(
      @PathVariable Long loungePostId, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(loungePostCommandService.scrapPost(loungePostId, TEMP_USER_ID)), request);
  }

  @DeleteMapping("/api/v1/lounge/posts/{loungePostId}/scraps")
  @Override
  public ApiResponseBody<LoungePostScrapResponse> cancelScrapPost(
      @PathVariable Long loungePostId, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(loungePostCommandService.cancelScrapPost(loungePostId, TEMP_USER_ID)),
        request);
  }

  @GetMapping("/api/v1/lounge/posts")
  @Override
  public ApiResponseBody<List<LoungePostListResponse>> getPosts(HttpServletRequest request) {
    List<LoungePostListResponse> responses =
        loungePostQueryService.getPosts().stream().map(mapper::toResponse).toList();
    return ApiResponseBody.success(responses, request);
  }

  @GetMapping("/api/v1/lounge/posts/{loungePostId}")
  @Override
  public ApiResponseBody<LoungePostDetailResponse> getPostDetail(
      @PathVariable Long loungePostId, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(loungePostQueryService.getPostDetail(loungePostId)), request);
  }
}
