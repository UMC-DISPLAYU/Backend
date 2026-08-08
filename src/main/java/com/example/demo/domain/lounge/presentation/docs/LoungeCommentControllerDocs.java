package com.example.demo.domain.lounge.presentation.docs;

import com.example.demo.domain.lounge.presentation.request.LoungeCommentRequest;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentLikeResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeCommentListResponse;
import com.example.demo.domain.lounge.presentation.response.LoungePostCursorResponse;
import com.example.demo.domain.lounge.presentation.response.LoungeReplyCursorResponse;
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

@Tag(name = "Lounge Comment", description = "라운지 댓글 API")
@SecurityRequirement(name = "Authorization")
public interface LoungeCommentControllerDocs {

  @Operation(
      summary = "라운지 댓글 생성",
      description = "라운지 게시글에 댓글을 생성합니다. 작가 전용 카테고리 게시글은 작가 인증이 필요합니다.")
  @ApiResponse(responseCode = "401", description = "로그인 필요")
  @ApiResponse(responseCode = "403", description = "작가 인증 필요")
  @ApiResponse(
      responseCode = "201",
      description = "라운지 댓글 생성 성공",
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
                                "loungeCommentId": 10,
                                "parentCommentId": null,
                                "content": "저도 인상 깊게 본 전시예요.",
                                "imageUrls": ["https://example.com/lounge/comments/10.jpg"],
                                "commentStatus": "ACTIVE",
                                "writer": {
                                  "userId": 1,
                                  "nickname": "전시러버",
                                  "profileImageUrl": "https://example.com/profiles/1.jpg"
                                },
                                "createdAt": "2026-08-04T12:00:00",
                                "updatedAt": "2026-08-04T12:00:00",
                                "likeCount": 0,
                                "replyCount": 0,
                                "isLiked": false,
                                "isMyComment": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/posts/1/comments"
                            }
                          }
                          """)))
  ApiResponseBody<LoungeCommentListResponse> createComment(
      @PathVariable Long loungePostId,
      @Valid @RequestBody LoungeCommentRequest loungeCommentRequest,
      AuthUser user,
      HttpServletRequest request);

  @Operation(
      summary = "라운지 답글 생성",
      description = "라운지 댓글에 답글을 생성합니다. 작가 전용 카테고리 게시글은 작가 인증이 필요합니다.")
  @ApiResponse(responseCode = "401", description = "로그인 필요")
  @ApiResponse(responseCode = "403", description = "작가 인증 필요")
  @ApiResponse(
      responseCode = "201",
      description = "라운지 답글 생성 성공",
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
                                "loungeCommentId": 11,
                                "parentCommentId": 10,
                                "content": "저도 다음 전시를 기대하고 있어요.",
                                "imageUrls": [],
                                "commentStatus": "ACTIVE",
                                "writer": {
                                  "userId": 2,
                                  "nickname": "작품수집가",
                                  "profileImageUrl": null
                                },
                                "createdAt": "2026-08-04T12:10:00",
                                "updatedAt": "2026-08-04T12:10:00",
                                "likeCount": 0,
                                "replyCount": 0,
                                "isLiked": false,
                                "isMyComment": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:10:00",
                              "path": "/api/v1/lounge/comments/10/replies"
                            }
                          }
                          """)))
  ApiResponseBody<LoungeCommentListResponse> createReply(
      @PathVariable Long parentCommentId,
      @Valid @RequestBody LoungeCommentRequest loungeCommentRequest,
      AuthUser user,
      HttpServletRequest request);

  @Operation(summary = "라운지 댓글 삭제", description = "작성자가 라운지 댓글 또는 답글을 삭제합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 댓글 삭제 성공",
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
                              "path": "/api/v1/lounge/comments/10"
                            }
                          }
                          """)))
  ApiResponseBody<Void> deleteComment(
      @PathVariable Long loungeCommentId, AuthUser user, HttpServletRequest request);

  @Operation(
      summary = "라운지 댓글 좋아요",
      description = "라운지 댓글 또는 답글에 좋아요를 추가합니다. 작가 전용 카테고리 게시글은 작가 인증이 필요합니다.")
  @ApiResponse(responseCode = "401", description = "로그인 필요")
  @ApiResponse(responseCode = "403", description = "작가 인증 필요")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 댓글 좋아요 성공",
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
                                "loungeCommentId": 10,
                                "isLiked": true,
                                "likeCount": 3
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/comments/10/likes"
                            }
                          }
                          """)))
  ApiResponseBody<LoungeCommentLikeResponse> likeComment(
      @PathVariable Long loungeCommentId, AuthUser user, HttpServletRequest request);

  @Operation(
      summary = "라운지 댓글 좋아요 취소",
      description = "라운지 댓글 또는 답글 좋아요를 취소합니다. 작가 전용 카테고리 게시글은 작가 인증이 필요합니다.")
  @ApiResponse(responseCode = "401", description = "로그인 필요")
  @ApiResponse(responseCode = "403", description = "작가 인증 필요")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 댓글 좋아요 취소 성공",
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
                                "loungeCommentId": 10,
                                "isLiked": false,
                                "likeCount": 2
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/comments/10/likes"
                            }
                          }
                          """)))
  ApiResponseBody<LoungeCommentLikeResponse> cancelLikeComment(
      @PathVariable Long loungeCommentId, AuthUser user, HttpServletRequest request);

  @Operation(
      summary = "라운지 댓글 목록 조회",
      description =
          """
          게시글의 댓글 목록을 커서 방식으로 조회합니다.
          작가 전용 카테고리 게시글의 댓글 조회에는 작가 인증이 필요합니다.
          ACTIVE 상태이면서 삭제되지 않은 부모 댓글을 반환합니다.
          DELETED 상태인 부모 댓글은 활성 답글이 남아 있을 때만 반환하며, commentStatus는 DELETED, content는 빈 문자열, imageUrls는 빈 배열입니다.
          HIDDEN 상태인 부모 댓글과 활성 답글이 없는 삭제된 부모 댓글은 반환하지 않습니다.
          """)
  @ApiResponse(responseCode = "401", description = "작가 전용 게시글 댓글 조회 시 로그인 필요")
  @ApiResponse(responseCode = "403", description = "작가 전용 게시글 댓글 조회 시 작가 인증 필요")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 댓글 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Deleted parent comment with active replies",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "comments": [
                                  {
                                    "loungeCommentId": 10,
                                    "parentCommentId": null,
                                    "content": "",
                                    "imageUrls": [],
                                    "commentStatus": "DELETED",
                                    "writer": {
                                      "userId": 2,
                                      "nickname": "사용자",
                                      "profileImageUrl": null
                                    },
                                    "createdAt": "2026-08-01T12:00:00",
                                    "updatedAt": "2026-08-02T12:00:00",
                                    "likeCount": 0,
                                    "replyCount": 1,
                                    "isLiked": false,
                                    "isMyComment": false
                                  }
                                ],
                                "nextCursorId": null,
                                "size": 10,
                                "hasNext": false
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-02T12:00:00",
                              "path": "/api/v1/lounge/posts/1/comments"
                            }
                          }
                          """)))
  ApiResponseBody<LoungeCommentCursorResponse> getComments(
      @PathVariable Long loungePostId,
      @Parameter(description = "마지막으로 조회한 댓글 ID. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          Long cursorId,
      @Parameter(description = "한 번에 불러올 댓글 개수") @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);

  @Operation(
      summary = "라운지 답글 목록 조회",
      description =
          "댓글의 활성 답글 목록을 커서 방식으로 조회합니다. 부모 댓글이 삭제되어도 남아 있는 답글을 조회할 수 있으며, 작가 전용 카테고리 게시글은 작가 인증이 필요합니다.")
  @ApiResponse(responseCode = "401", description = "작가 전용 게시글 답글 조회 시 로그인 필요")
  @ApiResponse(responseCode = "403", description = "작가 전용 게시글 답글 조회 시 작가 인증 필요")
  @ApiResponse(
      responseCode = "200",
      description = "라운지 답글 목록 조회 성공",
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
                                "replies": [
                                  {
                                    "loungeCommentId": 11,
                                    "parentCommentId": 10,
                                    "content": "저도 다음 전시를 기대하고 있어요.",
                                    "imageUrls": [],
                                    "commentStatus": "ACTIVE",
                                    "writer": {
                                      "userId": 2,
                                      "nickname": "작품수집가",
                                      "profileImageUrl": null
                                    },
                                    "createdAt": "2026-08-04T12:10:00",
                                    "updatedAt": "2026-08-04T12:10:00",
                                    "likeCount": 2,
                                    "isLiked": true,
                                    "isMyComment": false
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
                              "path": "/api/v1/lounge/comments/10/replies"
                            }
                          }
                          """)))
  ApiResponseBody<LoungeReplyCursorResponse> getReplies(
      @PathVariable Long parentCommentId,
      @Parameter(description = "마지막으로 조회한 답글 ID. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          Long cursorId,
      @Parameter(description = "한 번에 불러올 답글 개수") @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);

  @Operation(
      summary = "내가 댓글을 작성한 라운지 게시글 조회",
      description = "로그인 사용자가 댓글 또는 답글을 작성한 게시글 중 현재 접근 가능한 카테고리의 게시글을 중복 없이 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내가 댓글을 작성한 라운지 게시글 조회 성공",
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
                                "nextCursorId": 24,
                                "size": 10,
                                "hasNext": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/lounge/me/comments"
                            }
                          }
                          """)))
  ApiResponseBody<LoungePostCursorResponse> getMyComments(
      @Parameter(description = "마지막 게시글의 최근 댓글 ID. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          Long cursorId,
      @Parameter(description = "한 번에 불러올 게시글 개수")
          @RequestParam(defaultValue = "10")
          @Min(1) @Max(50) int size,
      AuthUser user,
      HttpServletRequest request);
}
