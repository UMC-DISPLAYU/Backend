package com.example.demo.domain.displaycommunication.presentation.docs;

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
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Display Review", description = "전시 후기 API")
public interface DisplayReviewApiDocs {

  @Operation(
      summary = "전시 후기 목록 조회",
      description = "전시 후기와 답글을 최신순 커서 페이지네이션으로 조회합니다. 비회원도 조회할 수 있습니다.")
  @ApiResponse(responseCode = "200", description = "전시 후기 목록 조회 성공")
  @ApiResponse(responseCode = "404", description = "전시 없음")
  ApiResponseBody<DisplayReviewListResponse> getReviews(
      @Parameter(description = "전시 ID", example = "1") Long displayId,
      @Parameter(description = "마지막으로 조회한 후기 ID", example = "20") Long cursorId,
      @Parameter(description = "조회 개수(최대 50)", example = "10") int size,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "전시 후기 답글 목록 조회",
      description = "특정 후기의 답글을 등록순 커서 페이지네이션으로 조회합니다. 비회원도 조회할 수 있습니다.")
  @ApiResponse(responseCode = "200", description = "전시 후기 답글 목록 조회 성공")
  @ApiResponse(responseCode = "404", description = "전시 또는 후기 없음")
  ApiResponseBody<DisplayReviewReplyListResponse> getReviewReplies(
      @Parameter(description = "전시 ID", example = "1") Long displayId,
      @Parameter(description = "후기 ID", example = "1") Long displayReviewId,
      @Parameter(description = "마지막으로 조회한 답글 ID", example = "20") Long cursorId,
      @Parameter(description = "조회 개수(최대 50)", example = "10") int size,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "전시 후기 작성", description = "사용자가 전시에 대한 후기를 작성합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "전시 후기 작성 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display review create success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "displayReviewId": 1,
                                "content": "작품들이 전시 주제와 잘 연결되어 있어 인상 깊었어요.",
                                "createdAt": "2026-07-23T20:00:00",
                                "displayId": 1,
                                "userId": 2,
                                "images": [
                                  {
                                    "reviewImageId": 1,
                                    "imageUrl": "https://cdn.displayu.com/reviews/1.jpg",
                                    "width": 1200,
                                    "height": 800,
                                    "sortOrder": 0
                                  }
                                ]
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-23T20:00:00",
                              "path": "/api/v1/display/1/reviews"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "400",
      description = "후기 내용 또는 이미지 검증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Invalid display review content",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "INVALID_INPUT_VALUE",
                              "message": "입력값이 올바르지 않습니다.",
                              "details": [
                                {
                                  "field": "content",
                                  "message": "후기 내용은 필수입니다."
                                }
                              ]
                            },
                            "meta": {
                              "timestamp": "2026-07-23T20:00:00",
                              "path": "/api/v1/display/1/reviews"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "403",
      description = "후기를 작성할 수 없는 전시",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display review not writable",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "DISPLAY_REVIEW_NOT_WRITABLE",
                              "message": "진행 중인 공개 전시에만 후기를 작성할 수 있습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-07-23T20:00:00",
                              "path": "/api/v1/display/1/reviews"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "전시 또는 사용자 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(
                    name = "Display not found",
                    value =
                        """
                        {
                          "resultType": "FAIL",
                          "success": null,
                          "error": {
                            "code": "DISPLAY_NOT_FOUND",
                            "message": "전시를 찾을 수 없습니다.",
                            "details": null
                          },
                          "meta": {
                            "timestamp": "2026-07-23T20:00:00",
                            "path": "/api/v1/display/1/reviews"
                          }
                        }
                        """),
                @ExampleObject(
                    name = "User not found",
                    value =
                        """
                        {
                          "resultType": "FAIL",
                          "success": null,
                          "error": {
                            "code": "USER_NOT_FOUND",
                            "message": "사용자를 찾을 수 없습니다.",
                            "details": null
                          },
                          "meta": {
                            "timestamp": "2026-07-23T20:00:00",
                            "path": "/api/v1/display/1/reviews"
                          }
                        }
                        """)
              }))
  @ApiResponse(
      responseCode = "409",
      description = "이미 작성한 후기",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display review already exists",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "DISPLAY_REVIEW_ALREADY_EXISTS",
                              "message": "이미 해당 전시의 후기를 작성했습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-07-23T20:00:00",
                              "path": "/api/v1/display/1/reviews"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<DisplayReviewResponse> createReview(
      @Parameter(description = "후기를 작성할 전시 ID", example = "1") Long displayId,
      @Parameter(hidden = true) AuthUser user,
      @Valid CreateDisplayReviewRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "전시 후기 답글 작성", description = "전시 후기에 답글을 작성합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "전시 후기 답글 작성 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display review reply create success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "displayReviewReplyId": 1,
                                "createdAt": "2026-07-24T01:00:00",
                                "content": "좋은 후기 감사합니다.",
                                "displayReviewId": 1,
                                "userId": 2,
                                "nickname": "달의작업실",
                                "isTeamMember": true,
                                "images": [
                                  {
                                    "displayReviewReplyImageId": 1,
                                    "imageUrl": "https://cdn.displayu.com/review-replies/1.jpg",
                                    "width": 1200,
                                    "height": 800,
                                    "sortOrder": 0
                                  }
                                ]
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-24T01:00:00",
                              "path": "/api/v1/display/1/reviews/1/replies"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "400", description = "답글 내용 또는 이미지 검증 실패")
  @ApiResponse(responseCode = "401", description = "인증 필요")
  @ApiResponse(responseCode = "404", description = "전시, 후기 또는 사용자 없음")
  ApiResponseBody<DisplayReviewReplyResponse> createReviewReply(
      @Parameter(description = "전시 ID", example = "1") Long displayId,
      @Parameter(description = "답글을 작성할 후기 ID", example = "1") Long displayReviewId,
      @Parameter(hidden = true) AuthUser user,
      @Valid CreateDisplayReviewReplyRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "전시 후기 좋아요", description = "전시 후기 좋아요를 등록하거나 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "전시 후기 좋아요 토글 성공",
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
                                "displayReviewId": 1,
                                "liked": true,
                                "likeCount": 12,
                                "createdAt": "2026-07-24T13:00:00",
                                "deletedAt": null
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-24T13:00:00",
                              "path": "/api/v1/display/1/reviews/1/like"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "404", description = "전시 후기 또는 사용자 없음")
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<DisplayReviewLikeResponse> reviewLike(
      @Parameter(description = "전시 ID", example = "1") Long displayId,
      @Parameter(description = "좋아요를 누를 후기 ID", example = "1") Long displayReviewId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "전시 후기 삭제", description = "작성자가 자신의 전시 후기를 삭제합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "전시 후기 삭제 성공",
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
                                "displayReviewId": 1,
                                "deletedAt": "2026-07-24T16:00:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-24T16:00:00",
                              "path": "/api/v1/display/1/reviews/1"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "403", description = "후기 작성자가 아님")
  @ApiResponse(responseCode = "401", description = "인증 필요")
  @ApiResponse(responseCode = "404", description = "전시 후기 또는 사용자 없음")
  ApiResponseBody<DeletedDisplayReviewResponse> deleteReview(
      @Parameter(description = "전시 ID", example = "1") Long displayId,
      @Parameter(description = "삭제할 후기 ID", example = "1") Long displayReviewId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "전시 후기 답글 삭제", description = "작성자가 자신의 전시 후기 답글을 삭제합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "전시 후기 답글 삭제 성공",
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
                                "displayReviewReplyId": 1,
                                "deletedAt": "2026-07-24T17:00:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-24T17:00:00",
                              "path": "/api/v1/display/1/reviews/1/reply/1"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "403", description = "답글 작성자가 아님")
  @ApiResponse(responseCode = "401", description = "인증 필요")
  @ApiResponse(responseCode = "404", description = "전시, 후기, 답글 또는 사용자 없음")
  ApiResponseBody<DeletedDisplayReviewReplyResponse> deleteReviewReply(
      @Parameter(description = "전시 ID", example = "1") Long displayId,
      @Parameter(description = "후기 ID", example = "1") Long displayReviewId,
      @Parameter(description = "삭제할 답글 ID", example = "1") Long displayReviewReplyId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "전시 후기 답글 좋아요", description = "전시 후기 답글 좋아요를 등록하거나 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "전시 후기 답글 좋아요 토글 성공",
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
                                "displayReviewReplyId": 1,
                                "liked": true,
                                "likeCount": 3,
                                "createdAt": "2026-07-24T17:30:00",
                                "deletedAt": null
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-24T17:30:00",
                              "path": "/api/v1/display/1/reviews/1/reply/1/like"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "404", description = "전시, 후기, 답글 또는 사용자 없음")
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<DisplayReviewReplyLikeResponse> reviewReplyLike(
      @Parameter(description = "전시 ID", example = "1") Long displayId,
      @Parameter(description = "후기 ID", example = "1") Long displayReviewId,
      @Parameter(description = "좋아요를 누를 답글 ID", example = "1") Long displayReviewReplyId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);
}
