package com.example.demo.domain.artworkcommunication.presentation.docs;

import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkQuestionRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkQuestionResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

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
      CreateArtworkQuestionRequest request,
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
      UpdateArtworkQuestionRequest request,
      HttpServletRequest httpServletRequest);
}
