package com.example.demo.domain.artworkcommunication.presentation.docs;

import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingLikeResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingReplyLikeResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingReplyListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingReplyResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.DeletedArtworkFeelingReplyResponse;
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
      summary = "작품 감상평 목록 조회",
      description = "작품 방명록 감상 탭에서 감상평 목록을 조회합니다. 비회원도 조회할 수 있습니다.")
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
                                      "nickname": "User1",
                                      "isCreator": false
                                    },
                                    "likeCount": 13,
                                    "replyCount": 2
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

  @Operation(summary = "작품 감상평 답변 목록 조회", description = "감상평의 답변 목록을 조회합니다. 비회원도 조회할 수 있습니다.")
  @ApiResponse(
      responseCode = "200",
      description = "감상평 답변 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling reply list success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "replies": [
                                  {
                                    "feelingReplyId": 8,
                                    "content": "감사합니다.",
                                    "createdAt": "2026-07-26T03:30:00",
                                    "user": {
                                      "userId": 2,
                                      "nickname": "고상준",
                                      "isCreator": true
                                    },
                                    "likeCount": 3
                                  }
                                ],
                                "nextCursorId": null,
                                "size": 3,
                                "hasNext": false
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-26T03:30:00",
                              "path": "/api/v1/artworks/3/feelings/7/replies"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "404", description = "작품 또는 감상평 없음")
  ApiResponseBody<ArtworkFeelingReplyListResponse> getFeelingReplies(
      @Parameter(description = "감상평이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "답변을 조회할 감상평 ID", example = "7") Long feelingId,
      @Parameter(description = "마지막으로 조회한 답변 ID. 첫 요청이면 전달하지 않음", example = "8")
          @RequestParam(required = false)
          @Positive Long cursorId,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "작품 감상평 작성", description = "로그인 사용자가 감상평을 작성합니다. 해당 작품의 작가도 작성할 수 있습니다.")
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
                                "feelingId": 15,
                                "userId": 4,
                                "nickname": "고상준"
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
      responseCode = "400",
      description = "답변 내용 검증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(
                    name = "Blank feeling reply content",
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
                            "timestamp": "2026-06-30T23:20:00",
                            "path": "/api/v1/artworks/3/feelings/15/reply"
                          }
                        }
                        """),
                @ExampleObject(
                    name = "Too long feeling reply content",
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
                                "message": "감상평 답변은 300자 이하로 작성해주세요."
                              }
                            ]
                          },
                          "meta": {
                            "timestamp": "2026-06-30T23:20:00",
                            "path": "/api/v1/artworks/3/feelings/15/reply"
                          }
                        }
                        """)
              }))
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

  @Operation(summary = "작품 감상평 답변 삭제", description = "작성자가 본인이 작성한 감상평 답변을 삭제합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "감상평 답변 삭제 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling reply delete success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "feelingReplyId": 8,
                                "deletedAt": "2026-07-26T03:40:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-26T03:40:00",
                              "path": "/api/v1/artworks/3/feelings/7/reply/8"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "403", description = "감상평 답변 삭제 권한 없음")
  @ApiResponse(responseCode = "404", description = "작품, 사용자, 감상평 또는 감상평 답변 없음")
  ApiResponseBody<DeletedArtworkFeelingReplyResponse> deleteFeelingReply(
      @Parameter(description = "감상평이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "답변이 속한 감상평 ID", example = "7") Long feelingId,
      @Parameter(description = "삭제할 감상평 답변 ID", example = "8") Long feelingReplyId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
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

  @Operation(summary = "작품 감상평 좋아요 토글", description = "감상평 좋아요를 등록하거나 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "감상평 좋아요 토글 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling like success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "feelingId": 7,
                                "liked": true,
                                "likeCount": 13,
                                "createdAt": "2026-06-30T23:20:00",
                                "deletedAt": null
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-06-30T23:40:00",
                              "path": "/api/v1/artworks/3/feelings/7/like"
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
                      name = "Artwork feeling like not found",
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
                              "timestamp": "2026-06-30T23:40:00",
                              "path": "/api/v1/artworks/3/feelings/7/like"
                            }
                          }
                          """)))
  ApiResponseBody<ArtworkFeelingLikeResponse> feelingLike(
      @Parameter(description = "감상평이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "좋아요를 토글할 감상평 ID", example = "7") Long feelingId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "작품 감상평 답변 좋아요 토글", description = "감상평 답변 좋아요를 등록하거나 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "감상평 답변 좋아요 토글 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork feeling reply like success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "feelingReplyId": 8,
                                "liked": true,
                                "likeCount": 3,
                                "createdAt": "2026-07-26T03:30:00",
                                "deletedAt": null
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-26T03:30:00",
                              "path": "/api/v1/artworks/3/feelings/7/reply/8/like"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "404", description = "작품, 사용자, 감상평 또는 감상평 답변 없음")
  ApiResponseBody<ArtworkFeelingReplyLikeResponse> feelingReplyLike(
      @Parameter(description = "감상평이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "답변이 속한 감상평 ID", example = "7") Long feelingId,
      @Parameter(description = "좋아요를 토글할 감상평 답변 ID", example = "8") Long feelingReplyId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
      HttpServletRequest httpServletRequest);
}
