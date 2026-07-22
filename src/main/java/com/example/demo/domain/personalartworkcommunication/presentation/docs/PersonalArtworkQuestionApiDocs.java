package com.example.demo.domain.personalartworkcommunication.presentation.docs;

import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Personal Artwork Question", description = "개인 작품 Q&A API")
public interface PersonalArtworkQuestionApiDocs {

  @Operation(summary = "개인 작품 질문 작성", description = "개인 작품 상세 화면에서 사용자가 질문을 작성합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 질문 작성 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork question create success",
                      value =
                          """
                                    {
                                      "resultType": "SUCCESS",
                                      "success": {
                                        "data": {
                                          "personalQuestionId": 1,
                                          "content": "이 작품에 사용한 재료가 궁금해요.",
                                          "isPublic": true,
                                          "answerStatus": "WAITING",
                                          "createdAt": "2026-07-20T22:20:00",
                                          "userId": 1
                                        }
                                      },
                                      "error": null,
                                      "meta": {
                                        "timestamp": "2026-07-20T22:20:00",
                                        "path": "/api/v1/personal-artworks/1/questions"
                                      }
                                    }
                                    """)))
  @ApiResponse(
      responseCode = "403",
      description = "작품 소유자의 질문 작성 시도",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork creator cannot write question",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "CREATOR_CANNOT_WRITE_QUESTION",
                              "message": "작업자는 본인 작품에 질문을 작성할 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-07-20T22:20:00",
                              "path": "/api/v1/personal-artworks/1/questions"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "400",
      description = "질문 내용 검증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Invalid personal artwork question content",
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
                                            "message": "질문 내용은 필수입니다."
                                          }
                                        ]
                                      },
                                      "meta": {
                                        "timestamp": "2026-07-20T22:20:00",
                                        "path": "/api/v1/personal-artworks/1/questions"
                                      }
                                    }
                                    """)))
  @ApiResponse(
      responseCode = "404",
      description = "개인 작품 또는 사용자 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork not found",
                      value =
                          """
                                    {
                                      "resultType": "FAIL",
                                      "success": null,
                                      "error": {
                                        "code": "PERSONAL_NOT_FOUND",
                                        "message": "개인 작품을 찾을 수 없습니다.",
                                        "details": null
                                      },
                                      "meta": {
                                        "timestamp": "2026-07-20T22:20:00",
                                        "path": "/api/v1/personal-artworks/1/questions"
                                      }
                                    }
                                    """)))
  ApiResponseBody<PersonalArtworkQuestionResponse> createPersonalQuestion(
      @Parameter(description = "질문을 작성할 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
      @Valid CreatePersonalArtworkQuestionRequest request,
      HttpServletRequest httpServletRequest);
}
