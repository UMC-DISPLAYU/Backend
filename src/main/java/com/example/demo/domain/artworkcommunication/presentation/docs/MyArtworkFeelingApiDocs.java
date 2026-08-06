package com.example.demo.domain.artworkcommunication.presentation.docs;

import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkFeelingListResponse;
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
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "My Artwork Feeling", description = "내가 남긴 작품 감상평 API")
public interface MyArtworkFeelingApiDocs {

  @Operation(
      summary = "내가 남긴 작품 감상평 목록 조회",
      description = "로그인 사용자가 전시 작품과 개인 작품에 작성한 삭제되지 않은 감상평을 구분 없이 최신순 커서 페이지네이션으로 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내가 남긴 작품 감상평 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "My artwork feeling list success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "feelings": [
                                  {
                                    "artworkId": 12,
                                    "personalArtworkId": null,
                                    "artworkName": "Blue Flow",
                                    "content": "푸른 색감과 흐르는 듯한 구성이 인상 깊었어요.",
                                    "createdAt": "2026-08-03T18:00:00"
                                  },
                                  {
                                    "artworkId": null,
                                    "personalArtworkId": 7,
                                    "artworkName": "개인 작업 1",
                                    "content": "작품의 질감 표현이 좋았습니다.",
                                    "createdAt": "2026-08-02T15:30:00"
                                  }
                                ],
                                "nextCursor": "MjAyNi0wOC0wMlQxNTozMDowMHwxfDE1",
                                "size": 10,
                                "hasNext": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-03T18:00:00",
                              "path": "/api/v1/artworks/feelings/me"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "400",
      description = "유효하지 않은 커서",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Invalid my artwork feeling cursor",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "INVALID_REQUEST",
                              "message": "유효하지 않은 cursor입니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-08-03T18:00:00",
                              "path": "/api/v1/artworks/feelings/me"
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
                      name = "My artwork feeling unauthorized",
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
                              "path": "/api/v1/artworks/feelings/me"
                            }
                          }
                          """)))
  ApiResponseBody<MyArtworkFeelingListResponse> getMyFeelings(
      @Parameter(hidden = true) AuthUser user,
      @Parameter(description = "이전 응답의 nextCursor. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          String cursor,
      @Parameter(description = "조회 개수(최대 50)", example = "10") @Min(1) @Max(50) int size,
      HttpServletRequest httpServletRequest);
}
