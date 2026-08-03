package com.example.demo.domain.artworkcommunication.presentation.docs;

import com.example.demo.domain.artworkcommunication.domain.type.AnswerStatus;
import com.example.demo.domain.artworkcommunication.presentation.response.ReceivedArtworkQuestionListResponse;
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

@Tag(name = "Received Artwork Question", description = "내가 받은 작품 질문 API")
public interface ReceivedArtworkQuestionApiDocs {

  @Operation(
      summary = "내가 받은 작품 질문 목록 조회",
      description = "로그인 사용자가 답변할 수 있는 전시 작품과 개인 작품의 질문을 답변 상태별로 최신순 커서 페이지네이션 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "내가 받은 작품 질문 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Received artwork question list success",
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
                                    "content": "작품의 제작 과정이 궁금합니다.",
                                    "isPublic": true,
                                    "answerStatus": "WAITING",
                                    "questionerId": 7,
                                    "questionerNickname": "artseeker_j",
                                    "createdAt": "2026-08-03T18:00:00"
                                  }
                                ],
                                "nextCursor": null,
                                "size": 10,
                                "hasNext": false
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-03T18:00:00",
                              "path": "/api/v1/artworks/questions/received"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "400", description = "유효하지 않은 답변 상태, 커서 또는 페이지 크기")
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<ReceivedArtworkQuestionListResponse> getReceivedQuestions(
      @Parameter(hidden = true) AuthUser user,
      @Parameter(description = "조회할 답변 상태", example = "WAITING", required = true) @RequestParam
          AnswerStatus answerStatus,
      @Parameter(description = "이전 응답의 nextCursor. 첫 요청이면 전달하지 않음") @RequestParam(required = false)
          String cursor,
      @Parameter(description = "조회 개수(최대 50)", example = "10") @Min(1) @Max(50) int size,
      HttpServletRequest httpServletRequest);
}
