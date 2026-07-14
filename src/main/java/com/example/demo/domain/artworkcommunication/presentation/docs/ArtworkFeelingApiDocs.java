package com.example.demo.domain.artworkcommunication.presentation.docs;

import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingReplyResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.DeletedArtworkFeelingResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.UpdatedArtworkFeelingResponse;
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
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Artwork Feeling", description = "작품 감상평 API")
public interface ArtworkFeelingApiDocs {

  @Operation(
      summary = "작품 감상평 목록 및 답변 조회",
      description = "작품 방명록 감상 탭에서 감상평 목록과 답변을 조회합니다. 비회원도 조회할 수 있습니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 감상평 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling list success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "feelings": [
                                  {
                                    "feelingId": 1,
                                    "content": "정말 감동적인 작품이에요.",
                                    "createdAt": "2026-06-30T22:10:00",
                                    "user": {
                                      "userId": 1,
                                      "nickname": "User1"
                                    },
                                    "replies": [
                                      {
                                        "userId": 1,
                                        "nickname": "고상준",
                                        "content": "감사합니다.",
                                        "createdAt": "2026-06-30T22:10:00",
                                        "isCreator": true
                                      }
                                    ]
                                  }
                                ],
                                "nextCursorId": 3,
                                "size": 3,
                                "hasNext": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/1/feelings"
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
                      name = "Artwork feeling list artwork not found",
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
                              "path": "/api/v1/artworks/1/feelings"
                            }
                          }
                          """)))
  ApiResponseBody<ArtworkFeelingListResponse> getFeelings(
      @Parameter(description = "감상평을 조회할 작품 ID", example = "1") Long artworkId,
      @Parameter(description = "마지막으로 조회한 감상평 ID. 첫 요청이면 전달하지 않음", example = "10")
          @RequestParam(required = false)
          @Positive Long cursorId,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "작품 감상평 작성", description = "작품 상세/방명록 화면에서 사용자가 감상평을 작성합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 감상평 작성 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling create success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "feelingId": 1,
                                "userId": 1,
                                "content": "정말 감동적인 작품이에요.",
                                "createdAt": "2026-06-30T22:10:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/1/feelings"
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
                              "path": "/api/v1/artworks/1/feelings"
                            }
                          }
                          """)))
  ApiResponseBody<ArtworkFeelingResponse> createFeeling(
      @Parameter(description = "감상평을 작성할 작품 ID", example = "1") Long artworkId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
      @Valid CreateArtworkFeelingRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "작품 감상평 답변 등록",
      description = "작품 감상평에 대한 답변을 작성합니다. 작가와 일반 회원 모두 작성할 수 있습니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 감상평 답변 등록 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling reply create success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "feelingReplyId": 8,
                                "content": "좋은 감상인 것 같아요",
                                "createdAt": "2026-06-30T23:20:00",
                                "updatedAt": "2026-06-30T23:20:00",
                                "deletedAt": null,
                                "feelingId": 15,
                                "userId": 4,
                                "nickname": "고상준",
                                "isCreator": false
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-06-30T23:20:00",
                              "path": "/api/v1/artworks/3/feelings/15/reply"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "작품, 사용자 또는 감상평 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling reply not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "FEELING_NOT_FOUND",
                              "message": "감상평을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/1/feelings/1/reply"
                            }
                          }
                          """)))
  ApiResponseBody<ArtworkFeelingReplyResponse> createFeelingReply(
      @Parameter(description = "감상평이 속한 작품 ID", example = "1") Long artworkId,
      @Parameter(description = "답변을 작성할 감상평 ID", example = "1") Long feelingId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
      @Valid CreateArtworkFeelingReplyRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "작품 감상평 수정", description = "사용자가 본인이 작성한 작품 감상평 내용을 수정합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 감상평 수정 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling update success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "feelingId": 1,
                                "content": "다시 보니 색감이 더 인상적이었어요.",
                                "updatedAt": "2026-06-30T22:20:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-06-30T22:20:00",
                              "path": "/api/v1/artworks/1/feelings/1"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "403",
      description = "감상평 수정 권한 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling forbidden",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "ARTWORK_FEELING_FORBIDDEN",
                              "message": "감상평에 대한 권한이 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T22:20:00",
                              "path": "/api/v1/artworks/1/feelings/1"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "작품, 사용자 또는 감상평 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "FEELING_NOT_FOUND",
                              "message": "감상평을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T22:20:00",
                              "path": "/api/v1/artworks/1/feelings/1"
                            }
                          }
                          """)))
  ApiResponseBody<UpdatedArtworkFeelingResponse> updateFeeling(
      @Parameter(description = "감상평이 속한 작품 ID", example = "1") Long artworkId,
      @Parameter(description = "수정할 감상평 ID", example = "1") Long feelingId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
      @Valid UpdateArtworkFeelingRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "작품 감상평 삭제", description = "사용자가 본인이 작성한 작품 감상평을 soft delete 방식으로 삭제합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 감상평 삭제 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling delete success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "feelingId": 1,
                                "deletedAt": "2026-06-30T22:10:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/1/feelings/1"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "403",
      description = "감상평 삭제 권한 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling delete forbidden",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "ARTWORK_FEELING_FORBIDDEN",
                              "message": "감상평에 대한 권한이 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/1/feelings/1"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "작품, 사용자 또는 감상평 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling delete not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "FEELING_NOT_FOUND",
                              "message": "감상평을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-06-30T22:10:00",
                              "path": "/api/v1/artworks/1/feelings/1"
                            }
                          }
                          """)))
  ApiResponseBody<DeletedArtworkFeelingResponse> deleteFeeling(
      @Parameter(description = "감상평이 속한 작품 ID", example = "1") Long artworkId,
      @Parameter(description = "삭제할 감상평 ID", example = "1") Long feelingId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
      HttpServletRequest httpServletRequest);
}
