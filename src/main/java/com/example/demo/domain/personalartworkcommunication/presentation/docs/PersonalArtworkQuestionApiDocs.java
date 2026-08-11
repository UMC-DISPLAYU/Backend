package com.example.demo.domain.personalartworkcommunication.presentation.docs;

import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkQuestionRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkQuestionReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkQuestionResponse;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.RequestParam;

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
          isMine은 로그인 사용자가 해당 질문 또는 답변의 작성자인지를 나타내며, 비회원은 false입니다.
          canReply는 개인 작품 소유자이면서 질문 상태가 WAITING일 때만 true입니다.
          질문/답변 좋아요 기능은 제공하지 않으므로 likeCount는 0, isLiked는 false입니다.
          접근할 수 없는 비공개 질문은 likeCount도 null로 마스킹합니다.
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
                                    "isMine": false,
                                    "canReply": false,
                                    "likeCount": 0,
                                    "isLiked": false,
                                    "answerStatus": "ANSWERED",
                                    "createdAt": "2026-07-23T17:00:00",
                                    "images": [
                                      {
                                        "personalQuestionImageId": 1,
                                        "imageUrl": "https://cdn.example.com/questions/1.jpg",
                                        "width": 1200,
                                        "height": 900,
                                        "sortOrder": 0
                                      }
                                    ],
                                    "user": {
                                      "userId": 2,
                                      "nickname": "관람객",
                                      "isCreator": false
                                    },
                                    "reply": {
                                      "personalQuestionReplyId": 1,
                                      "userId": 1,
                                      "nickname": "작품소유자",
                                      "isCreator": true,
                                      "content": "얇은 층을 열두 번 정도 겹쳤습니다.",
                                      "createdAt": "2026-07-23T17:10:00",
                                      "images": [],
                                      "likeCount": 0,
                                      "isLiked": false,
                                      "isMine": true
                                    }
                                  },
                                  {
                                    "personalQuestionId": 2,
                                    "content": null,
                                    "isPublic": false,
                                    "accessible": false,
                                    "isMine": false,
                                    "canReply": false,
                                    "likeCount": null,
                                    "isLiked": false,
                                    "answerStatus": "WAITING",
                                    "createdAt": "2026-07-23T17:15:00",
                                    "images": [],
                                    "user": null,
                                    "reply": null
                                  }
                                ],
                                "nextCursorId": 3,
                                "size": 10,
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
      @Parameter(description = "다음 페이지 조회를 위한 마지막 질문 ID. 첫 요청이면 전달하지 않음", example = "3")
          @RequestParam(required = false)
          @Positive Long cursorId,
      @Parameter(description = "한 번에 불러올 질문 개수", example = "10")
          @RequestParam(defaultValue = "10")
          @Min(1) @Max(50) int size,
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
                                "isCreator": true,
                                "images": [
                                  {
                                    "personalQuestionReplyImageId": 1,
                                    "imageUrl": "https://cdn.example.com/question-replies/1.jpg",
                                    "width": 1200,
                                    "height": 900,
                                    "sortOrder": 0
                                  }
                                ]
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
                                          "userId": 1,
                                          "images": [
                                            {
                                              "personalQuestionImageId": 1,
                                              "imageUrl": "https://cdn.example.com/questions/1.jpg",
                                              "width": 1200,
                                              "height": 900,
                                              "sortOrder": 0
                                            }
                                          ]
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
      responseCode = "400",
      description = "답변이 등록된 질문은 삭제 불가",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork question already answered",
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
                              "timestamp": "2026-08-11T12:00:00",
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
