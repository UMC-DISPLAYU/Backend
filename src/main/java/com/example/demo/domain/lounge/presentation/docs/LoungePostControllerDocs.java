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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 생성 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "게시글 생성 성공", value = POST_DETAIL_SUCCESS_EXAMPLE)))
  ApiResponseBody<LoungePostDetailResponse> createPost(
      @Valid @RequestBody LoungePostRequest loungePostRequest,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "라운지 게시글 수정", description = "작성자가 라운지 게시글을 수정합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 수정 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "게시글 수정 성공", value = POST_DETAIL_SUCCESS_EXAMPLE)))
  ApiResponseBody<LoungePostDetailResponse> updatePost(
      @PathVariable Long loungePostId,
      @Valid @RequestBody LoungePostRequest loungePostRequest,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "라운지 게시글 삭제", description = "작성자가 라운지 게시글을 삭제합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 삭제 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "게시글 삭제 성공", value = VOID_SUCCESS_EXAMPLE)))
  ApiResponseBody<Void> deletePost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 좋아요", description = "라운지 게시글에 좋아요를 추가합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 좋아요 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "게시글 좋아요 성공", value = POST_LIKE_SUCCESS_EXAMPLE)))
  ApiResponseBody<LoungePostLikeResponse> likePost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 좋아요 취소", description = "라운지 게시글 좋아요를 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 좋아요 취소 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "게시글 좋아요 취소 성공", value = POST_UNLIKE_SUCCESS_EXAMPLE)))
  ApiResponseBody<LoungePostLikeResponse> cancelLikePost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 스크랩", description = "라운지 게시글을 스크랩합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 스크랩 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "게시글 스크랩 성공", value = POST_SCRAP_SUCCESS_EXAMPLE)))
  ApiResponseBody<LoungePostScrapResponse> scrapPost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 스크랩 취소", description = "라운지 게시글 스크랩을 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 스크랩 취소 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "게시글 스크랩 취소 성공", value = POST_UNSCRAP_SUCCESS_EXAMPLE)))
  ApiResponseBody<LoungePostScrapResponse> cancelScrapPost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 목록 조회", description = "라운지 게시글 목록을 커서 방식으로 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "게시글 목록 조회 성공", value = POST_LIST_SUCCESS_EXAMPLE)))
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
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 상세 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "게시글 상세 조회 성공", value = POST_DETAIL_SUCCESS_EXAMPLE)))
  ApiResponseBody<LoungePostDetailResponse> getPostDetail(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "내 라운지 게시글 조회", description = "로그인 사용자가 작성한 게시글을 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내 라운지 게시글 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "내 게시글 조회 성공", value = POST_LIST_SUCCESS_EXAMPLE)))
  ApiResponseBody<LoungePostCursorResponse> getMyPosts(
      @Parameter(description = "마지막으로 조회한 게시글 ID. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          Long cursorId,
      @Parameter(description = "한 번에 불러올 게시글 개수")
          @RequestParam(defaultValue = "10")
          @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "내 라운지 스크랩 조회", description = "로그인 사용자가 스크랩한 게시글을 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내 라운지 스크랩 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "내 스크랩 조회 성공", value = POST_LIST_SUCCESS_EXAMPLE)))
  ApiResponseBody<LoungePostCursorResponse> getMyScrappedPosts(
      @Parameter(description = "마지막으로 조회한 스크랩 ID. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          Long cursorId,
      @Parameter(description = "한 번에 불러올 게시글 개수")
          @RequestParam(defaultValue = "10")
          @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);

  String POST_DETAIL_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "loungePostId": 1,
            "title": "전시 후기 공유합니다",
            "postImageUrls": ["https://cdn.displayu.com/lounge/post-1.png"],
            "content": "작품 배치가 인상적이었습니다.",
            "category": "REVIEW",
            "postStatus": "ACTIVE",
            "writer": {
              "userId": 1,
              "nickname": "maya01",
              "profileImageUrl": "https://cdn.displayu.com/profile/maya.png"
            },
            "createdAt": "2026-08-04T09:00:00",
            "updatedAt": "2026-08-04T09:00:00",
            "commentCount": 2,
            "likeCount": 5,
            "isLiked": true,
            "isScrapped": false,
            "isMyPost": true
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/lounge/posts/1" }
      }
      """;

  String POST_LIST_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "posts": [
              {
                "loungePostId": 1,
                "category": "REVIEW",
                "title": "전시 후기 공유합니다",
                "content": "작품 배치가 인상적이었습니다.",
                "postImageUrls": ["https://cdn.displayu.com/lounge/post-1.png"],
                "writer": {
                  "userId": 1,
                  "nickname": "maya01",
                  "profileImageUrl": "https://cdn.displayu.com/profile/maya.png"
                },
                "createdAt": "2026-08-04T09:00:00",
                "commentCount": 2,
                "likeCount": 5,
                "isLiked": true,
                "isMyPost": true
              }
            ],
            "nextCursorId": null,
            "size": 10,
            "hasNext": false
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/lounge/posts" }
      }
      """;

  String POST_LIKE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "loungePostId": 1,
            "isLiked": true,
            "likeCount": 5
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/lounge/posts/1/like" }
      }
      """;

  String POST_UNLIKE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "loungePostId": 1,
            "isLiked": false,
            "likeCount": 4
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/lounge/posts/1/like" }
      }
      """;

  String POST_SCRAP_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "loungePostId": 1,
            "isScrapped": true,
            "scrapCount": 3
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/lounge/posts/1/scrap" }
      }
      """;

  String POST_UNSCRAP_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "loungePostId": 1,
            "isScrapped": false,
            "scrapCount": 2
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/lounge/posts/1/scrap" }
      }
      """;

  String VOID_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": null
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/lounge/posts/1" }
      }
      """;
}
