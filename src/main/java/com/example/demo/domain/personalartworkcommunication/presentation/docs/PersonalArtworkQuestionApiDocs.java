package com.example.demo.domain.personalartworkcommunication.presentation.docs;

import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkQuestionResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionReplyResponse;
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

  @Operation(summary = "개인 작품 Q&A 답변 등록", description = "개인 작품 소유자가 해당 작품에 등록된 질문에 답변합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 Q&A 답변 등록 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork question reply create success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalQuestionReplyId": 1,
                                "createdAt": "2026-07-23T14:00:00",
                                "content": "아크릴 물감을 여러 번 겹쳐 칠했습니다.",
                                "personalQuestionId": 2,
                                "userId": 1,
                                "nickname": "작품소유자",
                                "isCreator": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-23T14:00:00",
                              "path": "/api/v1/personal-artworks/1/questions/2/reply"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "400",
      description = "답변 내용 검증 실패 또는 이미 답변된 질문",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(
                    name = "Invalid question reply content",
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
                                "message": "답변 내용은 필수입니다."
                              }
                            ]
                          },
                          "meta": {
                            "timestamp": "2026-07-23T14:00:00",
                            "path": "/api/v1/personal-artworks/1/questions/2/reply"
                          }
                        }
                        """),
                @ExampleObject(
                    name = "Question already answered",
                    value =
                        """
                        {
                          "resultType": "FAIL",
                          "success": null,
                          "error": {
                            "code": "PERSONAL_QUESTION_ALREADY_ANSWERED",
                            "message": "이미 답변 완료된 질문입니다.",
                            "details": null
                          },
                          "meta": {
                            "timestamp": "2026-07-23T14:00:00",
                            "path": "/api/v1/personal-artworks/1/questions/2/reply"
                          }
                        }
                        """)
              }))
  @ApiResponse(
      responseCode = "403",
      description = "질문 답변 권한 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork question reply forbidden",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "PERSONAL_QUESTION_REPLY_FORBIDDEN",
                              "message": "개인 작품 소유자만 질문에 답변할 수 있습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-07-23T14:00:00",
                              "path": "/api/v1/personal-artworks/1/questions/2/reply"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "개인 작품, 질문 또는 사용자 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork question not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "PERSONAL_QUESTION_NOT_FOUND",
                              "message": "개인 작품 질문을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-07-23T14:00:00",
                              "path": "/api/v1/personal-artworks/1/questions/2/reply"
                            }
                          }
                          """)))
  ApiResponseBody<PersonalArtworkQuestionReplyResponse> createQuestionReply(
      @Parameter(description = "질문이 속한 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "답변을 등록할 질문 ID", example = "2") Long personalQuestionId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
      @Valid CreatePersonalArtworkQuestionReplyRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "개인 작품 Q&A 작성", description = "개인 작품 상세 화면에서 사용자가 질문을 작성합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 Q&A 작성 성공",
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

  @Operation(
      summary = "개인 작품 Q&A 삭제",
      description = "사용자가 본인이 작성한 개인 작품 질문을 soft delete 방식으로 삭제합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 Q&A 삭제 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork question delete success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalQuestionId": 15,
                                "deletedAt": "2026-07-22T20:00:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-22T20:00:00",
                              "path": "/api/v1/personal-artworks/3/questions/15"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "403",
      description = "질문 삭제 권한 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork question forbidden",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "PERSONAL_ARTWORK_QUESTION_FORBIDDEN",
                              "message": "질문에 대한 권한이 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-07-22T20:00:00",
                              "path": "/api/v1/personal-artworks/3/questions/15"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "개인 작품, 사용자 또는 질문 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork question not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "PERSONAL_QUESTION_NOT_FOUND",
                              "message": "개인 작품 질문을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-07-22T20:00:00",
                              "path": "/api/v1/personal-artworks/3/questions/15"
                            }
                          }
                          """)))
  ApiResponseBody<DeletedPersonalArtworkQuestionResponse> deleteQuestion(
      @Parameter(description = "질문이 속한 개인 작품 ID", example = "3") Long personalArtworkId,
      @Parameter(description = "삭제할 질문 ID", example = "15") Long personalQuestionId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "27")
          Long userId,
      HttpServletRequest httpServletRequest);
}
