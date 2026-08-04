package com.example.demo.domain.personalartworkcommunication.presentation.docs;

import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkQuestionReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkQuestionResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionLikeResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionListResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkQuestionResponse;
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
import jakarta.validation.constraints.Positive;

@Tag(name = "Personal Artwork Question", description = "개인 작품 Q&A API")
public interface PersonalArtworkQuestionApiDocs {

  @Operation(
      summary = "개인 작품 질문 및 답변 목록 조회",
      description =
          """
          비회원도 호출할 수 있으며, 로그인한 경우 Authorization 헤더를 선택적으로 전달합니다.

          공개 질문은 모든 요청자에게 질문과 답변 전체를 제공합니다.
          비공개 질문은 질문 작성자 또는 개인 작품 소유자에게만 전체 내용을 제공합니다.
          권한이 없는 요청자에게도 목록 항목은 유지하지만 content, user, reply는 null로 마스킹합니다.

          accessible은 질문과 답변 원문을 조회할 수 있는지를 나타냅니다.
          canReply는 개인 작품 소유자이면서 질문 상태가 WAITING일 때만 true입니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 질문 및 답변 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork question list success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "questions": [
                                  {
                                    "personalQuestionId": 1,
                                    "content": "색을 몇 번 겹쳐 칠했나요?",
                                    "isPublic": true,
                                    "accessible": true,
                                    "canReply": false,
                                    "answerStatus": "ANSWERED",
                                    "createdAt": "2026-07-23T17:00:00",
                                    "user": {
                                      "userId": 2,
                                      "nickname": "관람객"
                                    },
                                    "reply": {
                                      "personalQuestionReplyId": 1,
                                      "userId": 1,
                                      "nickname": "작품소유자",
                                      "isCreator": true,
                                      "content": "얇은 층을 열두 번 정도 겹쳤습니다.",
                                      "createdAt": "2026-07-23T17:10:00"
                                    }
                                  },
                                  {
                                    "personalQuestionId": 2,
                                    "content": null,
                                    "isPublic": false,
                                    "accessible": false,
                                    "canReply": false,
                                    "answerStatus": "WAITING",
                                    "createdAt": "2026-07-23T17:15:00",
                                    "user": null,
                                    "reply": null
                                  }
                                ],
                                "nextCursorId": 3,
                                "size": 3,
                                "hasNext": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-23T17:20:00",
                              "path": "/api/v1/personal-artworks/1/questions"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "개인 작품 없음",
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
                              "code": "PERSONAL_ARTWORK_NOT_FOUND",
                              "message": "개인 작품을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-07-23T17:20:00",
                              "path": "/api/v1/personal-artworks/1/questions"
                            }
                          }
                          """)))
  ApiResponseBody<PersonalArtworkQuestionListResponse> getQuestions(
      @Parameter(description = "질문을 조회할 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "다음 페이지 조회를 위한 마지막 질문 ID", example = "3") @Positive Long cursorId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "개인 작품 질문 좋아요 등록 및 취소",
      description =
          """
          로그인 사용자가 개인 작품 질문의 좋아요 상태를 변경합니다.
          좋아요가 없거나 취소된 상태면 등록하고, 등록된 상태면 취소합니다.
          공개 질문은 모든 로그인 사용자가 처리할 수 있습니다.
          비공개 질문은 질문 작성자 또는 개인 작품 소유자만 처리할 수 있습니다.
          동시 요청은 질문 단위 비관적 쓰기 락으로 처리합니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 질문 좋아요 상태 변경 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork question like success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalQuestionId": 15,
                                "liked": true,
                                "likeCount": 12,
                                "createdAt": "2026-08-04T12:00:00",
                                "deletedAt": null
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-04T12:00:00",
                              "path": "/api/v1/personal-artworks/3/questions/15/like"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "401", description = "로그인 필요")
  @ApiResponse(responseCode = "403", description = "비공개 질문 접근 권한 없음")
  @ApiResponse(responseCode = "404", description = "개인 작품, 사용자 또는 질문 없음")
  ApiResponseBody<PersonalArtworkQuestionLikeResponse> questionLike(
      @Parameter(description = "질문이 등록된 개인 작품 ID", example = "3") Long personalArtworkId,
      @Parameter(description = "좋아요 상태를 변경할 개인 작품 질문 ID", example = "15") Long personalQuestionId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

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
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<PersonalArtworkQuestionReplyResponse> createQuestionReply(
      @Parameter(description = "질문이 속한 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "답변을 등록할 질문 ID", example = "2") Long personalQuestionId,
      @Parameter(hidden = true) AuthUser user,
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
                                        "code": "PERSONAL_ARTWORK_NOT_FOUND",
                                        "message": "개인 작품을 찾을 수 없습니다.",
                                        "details": null
                                      },
                                      "meta": {
                                        "timestamp": "2026-07-20T22:20:00",
                                        "path": "/api/v1/personal-artworks/1/questions"
                                      }
                                    }
                                    """)))
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<PersonalArtworkQuestionResponse> createPersonalQuestion(
      @Parameter(description = "질문을 작성할 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(hidden = true) AuthUser user,
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
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<DeletedPersonalArtworkQuestionResponse> deleteQuestion(
      @Parameter(description = "질문이 속한 개인 작품 ID", example = "3") Long personalArtworkId,
      @Parameter(description = "삭제할 질문 ID", example = "15") Long personalQuestionId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "개인 작품 Q&A 답변 삭제",
      description = "개인 작품 소유자가 답변을 soft delete하고 질문을 답변 대기 상태로 되돌립니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 Q&A 답변 삭제 성공",
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
                                "personalQuestionReplyId": 8,
                                "deletedAt": "2026-08-03T18:00:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-03T18:00:00",
                              "path": "/api/v1/personal-artworks/3/questions/15/reply/8"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "403", description = "답변 삭제 권한 없음")
  @ApiResponse(responseCode = "401", description = "인증 필요")
  @ApiResponse(responseCode = "404", description = "개인 작품, 사용자, 질문 또는 답변 없음")
  ApiResponseBody<DeletedPersonalArtworkQuestionReplyResponse> deleteQuestionReply(
      @Parameter(description = "질문이 속한 개인 작품 ID", example = "3") Long personalArtworkId,
      @Parameter(description = "질문 ID", example = "15") Long personalQuestionId,
      @Parameter(description = "삭제할 답변 ID", example = "8") Long personalQuestionReplyId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);
}
