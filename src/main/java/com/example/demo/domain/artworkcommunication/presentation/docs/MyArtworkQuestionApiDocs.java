package com.example.demo.domain.artworkcommunication.presentation.docs;

import com.example.demo.domain.artworkcommunication.presentation.response.MyArtworkQuestionListResponse;
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

@Tag(name = "My Artwork Question", description = "내가 한 작품 질문 API")
public interface MyArtworkQuestionApiDocs {

  @Operation(
      summary = "내가 한 작품 질문 목록 조회",
      description = "로그인 사용자가 전시 작품과 개인 작품에 작성한 삭제되지 않은 질문을 구분 없이 최신순 커서 페이지네이션으로 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내가 한 작품 질문 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "My artwork question list success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "questions": [
                                  {
                                    "questionId": 15,
                                    "personalQuestionId": null,
                                    "artworkId": 12,
                                    "personalArtworkId": null,
                                    "artworkName": "빛의 결",
                                    "content": "이 작품에서 사용한 재료가 궁금해요.",
                                    "isPublic": true,
                                    "answerStatus": "WAITING",
                                    "createdAt": "2026-08-03T18:00:00"
                                  },
                                  {
                                    "questionId": null,
                                    "personalQuestionId": 8,
                                    "artworkId": null,
                                    "personalArtworkId": 7,
                                    "artworkName": "개인 작업 1",
                                    "content": "작품의 제작 과정이 궁금합니다.",
                                    "isPublic": false,
                                    "answerStatus": "ANSWERED",
                                    "createdAt": "2026-08-02T15:30:00"
                                  }
                                ],
                                "nextCursor": "MjAyNi0wOC0wMlQxNTozMDowMHwxfDg",
                                "size": 10,
                                "hasNext": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-03T18:00:00",
                              "path": "/api/v1/artworks/questions/me"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "400", description = "유효하지 않은 커서 또는 페이지 크기")
  @ApiResponse(
      responseCode = "401",
      description = "인증 필요",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "My artwork question unauthorized",
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
                              "path": "/api/v1/artworks/questions/me"
                            }
                          }
                          """)))
  ApiResponseBody<MyArtworkQuestionListResponse> getMyQuestions(
      @Parameter(hidden = true) AuthUser user,
      @Parameter(description = "이전 응답의 nextCursor. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          String cursor,
      @Parameter(description = "조회 개수(최대 50)", example = "10") @Min(1) @Max(50) int size,
      HttpServletRequest httpServletRequest);
}
