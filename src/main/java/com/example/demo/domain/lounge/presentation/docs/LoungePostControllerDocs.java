package com.example.demo.domain.lounge.presentation.docs;

import com.example.demo.domain.lounge.domain.type.LoungePostCategory;
import com.example.demo.domain.lounge.presentation.request.LoungePostRequest;
import com.example.demo.domain.lounge.presentation.response.LoungePostCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostDetailResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostScrapResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Lounge Post", description = "라운지 게시글 API")
@SecurityRequirement(name = "Authorization")
public interface LoungePostControllerDocs {

  @Operation(summary = "라운지 게시글 생성", description = "라운지 게시글을 생성합니다.")
  ApiResponseBody<LoungePostDetailResponse> createPost(
      @Valid @RequestBody LoungePostRequest loungePostRequest,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "라운지 게시글 수정", description = "작성자가 라운지 게시글을 수정합니다.")
  ApiResponseBody<LoungePostDetailResponse> updatePost(
      @PathVariable Long loungePostId,
      @Valid @RequestBody LoungePostRequest loungePostRequest,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "라운지 게시글 삭제", description = "작성자가 라운지 게시글을 삭제합니다.")
  ApiResponseBody<Void> deletePost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 좋아요", description = "라운지 게시글에 좋아요를 추가합니다.")
  ApiResponseBody<LoungePostLikeResponse> likePost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 좋아요 취소", description = "라운지 게시글 좋아요를 취소합니다.")
  ApiResponseBody<LoungePostLikeResponse> cancelLikePost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 스크랩", description = "라운지 게시글을 스크랩합니다.")
  ApiResponseBody<LoungePostScrapResponse> scrapPost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 스크랩 취소", description = "라운지 게시글 스크랩을 취소합니다.")
  ApiResponseBody<LoungePostScrapResponse> cancelScrapPost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 목록 조회", description = "라운지 게시글 목록을 커서 방식으로 조회합니다.")
  ApiResponseBody<LoungePostCursorResponse> getPosts(
      @Parameter(description = "라운지 게시글 카테고리. 없으면 전체 조회") @RequestParam(required = false)
          LoungePostCategory category,
      @Parameter(description = "마지막으로 조회한 게시글 ID. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          Long cursorId,
      @Parameter(description = "한 번에 불러올 게시글 개수")
          @RequestParam(defaultValue = "10")
          @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "라운지 게시글 상세 조회", description = "라운지 게시글 상세 정보를 조회합니다.")
  ApiResponseBody<LoungePostDetailResponse> getPostDetail(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);
}
