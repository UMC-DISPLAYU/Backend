package com.example.demo.domain.artworkcommunication.presentation.docs;

import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionReplyResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.DeletedArtworkQuestionReplyResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.DeletedArtworkQuestionResponse;
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
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Artwork Question", description = "작품 Q&A 질문 API")
public interface ArtworkQuestionApiDocs {

  @Operation(
      summary = "작품 Q&A 질문 목록 조회",
      description =
          """
          비회원도 호출할 수 있으며, 로그인한 경우에만 Authorization 헤더를 선택적으로 전달합니다.

          공개 질문은 모든 요청자에게 질문과 답변 전체를 제공합니다.
          비공개 질문은 질문 작성자 또는 해당 작품의 참여 작가에게만 전체 내용을 제공합니다.
          권한이 없는 요청자에게도 목록 항목은 유지하지만 content, user, reply는 null로 마스킹합니다.

          accessible은 질문과 답변 원문을 조회할 수 있는지를 나타냅니다.
          canReply는 로그인 사용자가 현재 질문에 답변을 등록할 수 있는지를 나타내며,
          해당 작품의 isContact=true 담당 작가이고 질문 상태가 WAITING일 때만 true입니다.
          일반 참여 작가는 비공개 질문을 조회할 수 있지만 답변을 등록할 수 없습니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "작품 Q&A 질문 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork question list success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "questions": [
                                  {
                                    "questionId": 1,
                                    "content": "이 작품에서 사용한 재료가 궁금해요.",
                                    "isPublic": true,
                                    "accessible": true,
                                    "canReply": false,
                                    "answerStatus": "ANSWERED",
                                    "createdAt": "2026-06-30T22:10:00",
                                    "user": {
                                      "userId": 1,
                                      "nickname": "User1"
                                    },
                                    "reply": {
                                      "creatorId": 4,
                                      "creatorName": "고상준",
                                      "isCreator": true,
                                      "content": "캔버스에 유화를 사용했어요.",
                                      "createdAt": "2026-06-30T22:10:00"
                                    }
                                  },
                                  {
                                    "questionId": 2,
                                    "content": null,
                                    "isPublic": false,
                                    "accessible": false,
                                    "canReply": false,
                                    "answerStatus": "WAITING",
                                    "createdAt": "2026-06-30T22:15:00",
                                    "user": null,
                                    "reply": null
                                  }
                                ],
                                "nextCursorId": null,
                                "size": 3,
                                "hasNext": false
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/1/questions"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "작품 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "ARTWORK_NOT_FOUND",
                              "message": "작품을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/1/questions"
                            }
                          }
                          """)))
  ApiResponseBody<ArtworkQuestionListResponse> getQuestions(
      @Parameter(description = "질문 목록을 조회할 작품 ID", example = "1") Long artworkId,
      @Parameter(description = "마지막으로 조회한 질문 ID. 첫 요청이면 전달하지 않음", example = "3")
          @RequestParam(required = false)
          @Positive Long cursorId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "작품 Q&A 질문 등록",
      description = "로그인 사용자가 공개 또는 비공개 질문을 등록합니다. 해당 작품의 작가는 질문을 작성할 수 없습니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 Q&A 질문 등록 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork question create success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "questionId": 15,
                                "content": "붉은 배경과 회색 벽면을 대비시킨 이유가 궁금해요.",
                                "isPublic": true,
                                "answerStatus": "WAITING",
                                "createdAt": "2026-06-30T22:10:00",
                                "displayArtworkId": 3,
                                "userId": 27
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/3/questions"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "403",
      description = "해당 작품의 작가는 질문 작성 불가",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Creator cannot write question",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "CREATOR_CANNOT_WRITE_QUESTION",
                              "message": "작가는 본인 작품에 질문을 작성할 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/3/questions"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "400",
      description = "질문 내용 누락",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Invalid question content",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "INVALID_QUESTION_CONTENT",
                              "message": "질문 내용을 입력해주세요.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/3/questions"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "작품 또는 사용자 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "ARTWORK_NOT_FOUND",
                              "message": "작품을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/3/questions"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<ArtworkQuestionResponse> createQuestion(
      @Parameter(description = "질문을 등록할 작품 ID", example = "3") Long artworkId,
      @Parameter(hidden = true) AuthUser user,
      @Valid CreateArtworkQuestionRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "작품 Q&A 답변 등록", description = "QnA 담당 작업자가 작품 질문에 대한 답변을 등록합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 Q&A 답변 등록 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork question reply create success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "queReplyId": 8,
                                "content": "따뜻함과 고요함이 동시에 남아 있는 공간을 표현하고 싶어서 두 색면을 대비시켰습니다.",
                                "createdAt": "2026-06-30T23:20:00",
                                "questionId": 15,
                                "creatorId": 4,
                                "creatorName": "고상준"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-06-30T23:20:00",
                              "path": "/api/v1/artworks/3/questions/15/reply"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "400",
      description = "이미 답변 완료된 질문",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Question already answered",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "QUESTION_ALREADY_ANSWERED",
                              "message": "이미 답변 완료된 질문입니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T23:20:00",
                              "path": "/api/v1/artworks/3/questions/15/reply"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "403",
      description = "QnA 담당 작업자 아님",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "QnA contact forbidden",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "QNA_CONTACT_FORBIDDEN",
                              "message": "QnA 담당 작업자만 답변할 수 있습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T23:20:00",
                              "path": "/api/v1/artworks/3/questions/15/reply"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "작품, 사용자 또는 질문 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork question reply not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "QUESTION_NOT_FOUND",
                              "message": "질문을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T23:20:00",
                              "path": "/api/v1/artworks/3/questions/15/reply"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<ArtworkQuestionReplyResponse> createQuestionReply(
      @Parameter(description = "질문이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "답변을 등록할 질문 ID", example = "15") Long questionId,
      @Parameter(hidden = true) AuthUser user,
      @Valid CreateArtworkQuestionReplyRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "작품 Q&A 질문 수정", description = "사용자가 본인이 작성한 작품 Q&A 질문의 내용과 공개 여부를 수정합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 Q&A 질문 수정 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork question update success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "questionId": 15,
                                "content": "수정된 질문 내용입니다.",
                                "isPublic": false,
                                "answerStatus": "WAITING",
                                "createdAt": "2026-06-30T22:10:00",
                                "displayArtworkId": 3,
                                "userId": 27
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-06-30T23:05:00",
                              "path": "/api/v1/artworks/3/questions/15"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "400",
      description = "질문 내용 누락",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Invalid question content",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "INVALID_QUESTION_CONTENT",
                              "message": "질문 내용을 입력해주세요.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T23:05:00",
                              "path": "/api/v1/artworks/3/questions/15"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "403",
      description = "질문 수정 권한 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork question forbidden",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "ARTWORK_QUESTION_FORBIDDEN",
                              "message": "질문에 대한 권한이 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T23:05:00",
                              "path": "/api/v1/artworks/3/questions/15"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "작품, 사용자 또는 질문 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork question not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "QUESTION_NOT_FOUND",
                              "message": "질문을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T23:05:00",
                              "path": "/api/v1/artworks/3/questions/15"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<ArtworkQuestionResponse> updateQuestion(
      @Parameter(description = "질문이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "수정할 질문 ID", example = "15") Long questionId,
      @Parameter(hidden = true) AuthUser user,
      @Valid UpdateArtworkQuestionRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "작품 Q&A 질문 삭제",
      description = "사용자가 본인이 작성한 작품 Q&A 질문을 soft delete 방식으로 삭제합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 Q&A 질문 삭제 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork question delete success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "questionId": 15,
                                "deletedAt": "2026-06-30T23:10:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-06-30T23:10:00",
                              "path": "/api/v1/artworks/3/questions/15"
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
                      name = "Artwork question forbidden",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "ARTWORK_QUESTION_FORBIDDEN",
                              "message": "질문에 대한 권한이 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T23:10:00",
                              "path": "/api/v1/artworks/3/questions/15"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "작품, 사용자 또는 질문 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork question not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "QUESTION_NOT_FOUND",
                              "message": "질문을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T23:10:00",
                              "path": "/api/v1/artworks/3/questions/15"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<DeletedArtworkQuestionResponse> deleteQuestion(
      @Parameter(description = "질문이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "삭제할 질문 ID", example = "15") Long questionId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "작품 Q&A 답변 삭제",
      description = "답변 작성자가 답변을 soft delete하고 질문을 답변 대기 상태로 되돌립니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 Q&A 답변 삭제 성공",
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
                                "questionReplyId": 8,
                                "deletedAt": "2026-08-03T17:00:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-08-03T17:00:00",
                              "path": "/api/v1/artworks/3/questions/15/reply/8"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "403", description = "답변 삭제 권한 없음")
  @ApiResponse(responseCode = "401", description = "인증 필요")
  @ApiResponse(responseCode = "404", description = "작품, 사용자, 질문 또는 답변 없음")
  ApiResponseBody<DeletedArtworkQuestionReplyResponse> deleteQuestionReply(
      @Parameter(description = "질문이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "질문 ID", example = "15") Long questionId,
      @Parameter(description = "삭제할 답변 ID", example = "8") Long questionReplyId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);
}
