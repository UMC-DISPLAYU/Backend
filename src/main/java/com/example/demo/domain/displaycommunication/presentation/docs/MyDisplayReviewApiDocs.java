package com.example.demo.domain.displaycommunication.presentation.docs;

import com.example.demo.domain.displaycommunication.presentation.response.MyDisplayReviewListResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

@Tag(name = "My Display Review", description = "내가 남긴 전시 후기 API")
public interface MyDisplayReviewApiDocs {

  @Operation(
      summary = "내가 남긴 전시 후기 목록 조회",
      description = "로그인 사용자가 작성한 삭제되지 않은 전시 후기를 최신순 커서 페이지네이션으로 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내 전시 후기 목록 조회 성공",
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
                                "reviews": [
                                  {
                                    "displayReviewId": 20,
                                    "displayId": 3,
                                    "displayName": "빛의 결",
                                    "content": "전시 공간 구성도 좋고 작품들도 인상 깊었어요.",
                                    "createdAt": "2026-08-03T18:00:00"
                                  }
                                ],
                                "nextCursorId": 20,
                                "size": 10,
                                "hasNext": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-03T18:00:00",
                              "path": "/api/v1/display/reviews/me"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "My display review unauthorized",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-08-03T18:00:00",
                              "path": "/api/v1/display/reviews/me"
                            }
                          }
                          """)))
  ApiResponseBody<MyDisplayReviewListResponse> getMyReviews(
      @Parameter(hidden = true) AuthUser user,
      @Parameter(description = "마지막으로 조회한 후기 ID", example = "20") @Positive Long cursorId,
      @Parameter(description = "조회 개수(최대 50)", example = "10") @Min(1) @Max(50) int size,
      HttpServletRequest httpServletRequest);
}
