package com.example.demo.domain.artworkcommunication.presentation.docs;

import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionReplyResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.DeletedArtworkQuestionResponse;
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

@Tag(name = "Artwork Question", description = "작품 Q&A 질문 API")
public interface ArtworkQuestionApiDocs {

  @Operation(summary = "작품 Q&A 질문 등록", description = "작품 상세/방명록 화면에서 사용자가 공개 또는 비공개 질문을 등록합니다.")
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
                                "artQueId": 15,
                                "content": "붉은 배경과 회색 벽면을 대비시킨 이유가 궁금해요.",
                                "isPublic": true,
                                "answerStatus": "WAITING",
                                "createdAt": "2026-06-30T22:10:00",
                                "updatedAt": "2026-06-30T22:10:00",
                                "deletedAt": null,
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
  ApiResponseBody<ArtworkQuestionResponse> createQuestion(
      @Parameter(description = "질문을 등록할 작품 ID", example = "3") Long artworkId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "27")
          Long userId,
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
                                "updatedAt": "2026-06-30T23:20:00",
                                "deletedAt": null,
                                "artQueId": 15,
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
  ApiResponseBody<ArtworkQuestionReplyResponse> createQuestionReply(
      @Parameter(description = "질문이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "답변을 등록할 질문 ID", example = "15") Long questionId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "4")
          Long userId,
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
                                "artQueId": 15,
                                "content": "수정된 질문 내용입니다.",
                                "isPublic": false,
                                "answerStatus": "WAITING",
                                "createdAt": "2026-06-30T22:10:00",
                                "updatedAt": "2026-06-30T23:05:00",
                                "deletedAt": null,
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
  ApiResponseBody<ArtworkQuestionResponse> updateQuestion(
      @Parameter(description = "질문이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "수정할 질문 ID", example = "15") Long questionId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "27")
          Long userId,
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
                                "artQueId": 15,
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
  ApiResponseBody<DeletedArtworkQuestionResponse> deleteQuestion(
      @Parameter(description = "질문이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "삭제할 질문 ID", example = "15") Long questionId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "27")
          Long userId,
      HttpServletRequest httpServletRequest);
}
