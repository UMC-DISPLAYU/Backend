package com.example.demo.domain.displaycommunication.presentation.docs;

import com.example.demo.domain.displaycommunication.presentation.request.CreateDisplayReviewRequest;
import com.example.demo.domain.displaycommunication.presentation.response.DisplayReviewResponse;
import com.example.demo.global.response.ApiResponseBody;
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

  @Operation(summary = "전시 후기 작성", description = "사용자가 전시에 대한 후기를 작성합니다. 임시로 X-User-Id 헤더를 사용합니다.")
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
      description = "작성할 수 없는 전시 또는 전시 관계자",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
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
                        """),
                @ExampleObject(
                    name = "Display team member forbidden",
                    value =
                        """
                        {
                          "resultType": "FAIL",
                          "success": null,
                          "error": {
                            "code": "DISPLAY_TEAM_MEMBER_REVIEW_FORBIDDEN",
                            "message": "전시 팀에 속한 회원은 해당 전시의 후기를 작성할 수 없습니다.",
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
  ApiResponseBody<DisplayReviewResponse> createReview(
      @Parameter(description = "후기를 작성할 전시 ID", example = "1") Long displayId,
      @Parameter(description = "후기를 작성하는 사용자 ID", required = true, example = "2") Long userId,
      @Valid CreateDisplayReviewRequest request,
      HttpServletRequest httpServletRequest);
}
