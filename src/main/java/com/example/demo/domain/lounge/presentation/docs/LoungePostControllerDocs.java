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
      responseCode = "201",
      description = "라운지 게시글 생성 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "loungePostId": 1,
                                "title": "졸업전시 관람 후기",
                                "postImageUrls": ["https://example.com/lounge/posts/1.jpg"],
                                "content": "졸업전시 관람 후기를 공유합니다.",
                                "category": "DISPLAY_REVIEW",
                                "postStatus": "ACTIVE",
                                "writer": {
                                  "userId": 1,
                                  "nickname": "전시러버",
                                  "profileImageUrl": "https://example.com/profiles/1.jpg"
                                },
                                "createdAt": "2026-08-04T12:00:00",
                                "updatedAt": "2026-08-04T12:00:00",
                                "commentCount": 0,
                                "likeCount": 0,
                                "isLiked": false,
                                "isScrapped": false,
                                "isMyPost": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/posts"
                            }
                          }
                          """)))
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
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "loungePostId": 1,
                                "title": "수정된 졸업전시 관람 후기",
                                "postImageUrls": ["https://example.com/lounge/posts/1-updated.jpg"],
                                "content": "수정된 졸업전시 관람 후기를 공유합니다.",
                                "category": "DISPLAY_REVIEW",
                                "postStatus": "ACTIVE",
                                "writer": {
                                  "userId": 1,
                                  "nickname": "전시러버",
                                  "profileImageUrl": "https://example.com/profiles/1.jpg"
                                },
                                "createdAt": "2026-08-04T12:00:00",
                                "updatedAt": "2026-08-04T13:00:00",
                                "commentCount": 2,
                                "likeCount": 3,
                                "isLiked": false,
                                "isScrapped": false,
                                "isMyPost": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T13:00:00",
                              "path": "/api/v1/lounge/posts/1"
                            }
                          }
                          """)))
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
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": null
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/posts/1"
                            }
                          }
                          """)))
  ApiResponseBody<Void> deletePost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 좋아요", description = "라운지 게시글에 좋아요를 추가합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 좋아요 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "loungePostId": 1,
                                "isLiked": true,
                                "likeCount": 13
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/posts/1/likes"
                            }
                          }
                          """)))
  ApiResponseBody<LoungePostLikeResponse> likePost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 좋아요 취소", description = "라운지 게시글 좋아요를 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 좋아요 취소 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "loungePostId": 1,
                                "isLiked": false,
                                "likeCount": 12
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/posts/1/likes"
                            }
                          }
                          """)))
  ApiResponseBody<LoungePostLikeResponse> cancelLikePost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 스크랩", description = "라운지 게시글을 스크랩합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 스크랩 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "loungePostId": 1,
                                "isScrapped": true,
                                "scrapCount": 7
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/posts/1/scraps"
                            }
                          }
                          """)))
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
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "loungePostId": 1,
                                "isScrapped": false,
                                "scrapCount": 6
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/posts/1/scraps"
                            }
                          }
                          """)))
  ApiResponseBody<LoungePostScrapResponse> cancelScrapPost(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "라운지 게시글 목록 조회", description = "라운지 게시글 목록을 커서 방식으로 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 게시글 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "posts": [
                                  {
                                    "loungePostId": 10,
                                    "category": "DISPLAY_REVIEW",
                                    "title": "졸업전시 관람 후기",
                                    "content": "졸업전시 관람 후기를 공유합니다.",
                                    "postImageUrls": ["https://example.com/lounge/posts/10.jpg"],
                                    "writer": {
                                      "userId": 1,
                                      "nickname": "전시러버",
                                      "profileImageUrl": "https://example.com/profiles/1.jpg"
                                    },
                                    "createdAt": "2026-08-04T12:00:00",
                                    "commentCount": 4,
                                    "likeCount": 12,
                                    "isLiked": true,
                                    "isMyPost": false
                                  }
                                ],
                                "nextCursorId": 10,
                                "size": 10,
                                "hasNext": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/posts"
                            }
                          }
                          """)))
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
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "loungePostId": 1,
                                "title": "졸업전시 관람 후기",
                                "postImageUrls": ["https://example.com/lounge/posts/1.jpg"],
                                "content": "졸업전시 관람 후기를 공유합니다.",
                                "category": "DISPLAY_REVIEW",
                                "postStatus": "ACTIVE",
                                "writer": {
                                  "userId": 1,
                                  "nickname": "전시러버",
                                  "profileImageUrl": "https://example.com/profiles/1.jpg"
                                },
                                "createdAt": "2026-08-04T12:00:00",
                                "updatedAt": "2026-08-04T12:00:00",
                                "commentCount": 4,
                                "likeCount": 12,
                                "isLiked": true,
                                "isScrapped": true,
                                "isMyPost": false
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/posts/1"
                            }
                          }
                          """)))
  ApiResponseBody<LoungePostDetailResponse> getPostDetail(
      @PathVariable Long loungePostId, AuthUser user, HttpServletRequest request);

  @Operation(summary = "내 라운지 게시글 조회", description = "로그인 사용자가 작성한 게시글을 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내 라운지 게시글 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "posts": [
                                  {
                                    "loungePostId": 10,
                                    "category": "WORK_TIP",
                                    "title": "작업 팁을 공유합니다",
                                    "content": "전시 준비 과정에서 알게 된 팁입니다.",
                                    "postImageUrls": [],
                                    "writer": {
                                      "userId": 1,
                                      "nickname": "전시러버",
                                      "profileImageUrl": "https://example.com/profiles/1.jpg"
                                    },
                                    "createdAt": "2026-08-04T12:00:00",
                                    "commentCount": 2,
                                    "likeCount": 5,
                                    "isLiked": false,
                                    "isMyPost": true
                                  }
                                ],
                                "nextCursorId": null,
                                "size": 10,
                                "hasNext": false
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/me/posts"
                            }
                          }
                          """)))
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
              examples =
                  @ExampleObject(
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "posts": [
                                  {
                                    "loungePostId": 8,
                                    "category": "COLLABORATION",
                                    "title": "협업 전시 팀원을 구합니다",
                                    "content": "영상 작업에 함께할 팀원을 찾고 있습니다.",
                                    "postImageUrls": ["https://example.com/lounge/posts/8.jpg"],
                                    "writer": {
                                      "userId": 2,
                                      "nickname": "영상작가",
                                      "profileImageUrl": null
                                    },
                                    "createdAt": "2026-08-03T12:00:00",
                                    "commentCount": 3,
                                    "likeCount": 9,
                                    "isLiked": false,
                                    "isMyPost": false
                                  }
                                ],
                                "nextCursorId": 15,
                                "size": 10,
                                "hasNext": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/me/scraps"
                            }
                          }
                          """)))
  ApiResponseBody<LoungePostCursorResponse> getMyScrappedPosts(
      @Parameter(description = "마지막으로 조회한 스크랩 ID. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          Long cursorId,
      @Parameter(description = "한 번에 불러올 게시글 개수")
          @RequestParam(defaultValue = "10")
          @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);
}
