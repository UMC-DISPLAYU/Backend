package com.example.demo.domain.artworkcommunication.presentation.docs;

import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingReplyRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingLikeResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingReplyLikeResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingReplyListResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingReplyResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.DeletedArtworkFeelingReplyResponse;
import com.example.demo.domain.artworkcommunication.presentation.response.DeletedArtworkFeelingResponse;
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

@Tag(name = "Artwork Feeling", description = "작품 감상평 API")
public interface ArtworkFeelingApiDocs {

  @Operation(
      summary = "작품 감상평 목록 조회",
      description =
          "작품 방명록 감상 탭에서 감상평 목록을 조회합니다. 작성자의 프로필 이미지 URL을 user.profileImageUrl로 반환합니다. 삭제된 감상평은 활성 답글이 남아 있을 때만 isDeleted=true 상태로 유지되며 마지막 답글이 삭제되면 목록에서 제외됩니다. 로그인 사용자의 좋아요 여부는 isLiked, 본인 작성 여부는 isMine으로 반환하며 비회원 조회 시 두 값은 false입니다.")
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
                                    "isDeleted": false,
                                    "isMine": true,
                                    "user": {
                                      "userId": 1,
                                      "nickname": "User1",
                                      "profileImageUrl": "https://cdn.example.com/profile/1.jpg",
                                      "isCreator": false
                                    },
                                    "images": [
                                      {
                                        "feelingImageId": 1,
                                        "imageUrl": "https://cdn.example.com/feeling-1.jpg",
                                        "width": 1200,
                                        "height": 900,
                                        "sortOrder": 0
                                      }
                                    ],
                                    "likeCount": 13,
                                    "isLiked": true,
                                    "replyCount": 2
                                  }
                                ],
                                "nextCursorId": 3,
                                "size": 10,
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
      @Parameter(description = "한 번에 불러올 감상평 개수", example = "10")
          @RequestParam(defaultValue = "10")
          @Min(1) @Max(50) int size,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "작품 감상평 답변 목록 조회",
      description =
          "감상평의 삭제되지 않은 답변 목록을 조회하며 작성자의 프로필 이미지 URL을 user.profileImageUrl로 반환합니다. 원본 감상평이 삭제된 경우에도 답변은 유지되고 조회할 수 있습니다. 로그인 사용자의 좋아요 여부는 isLiked로 반환하며, 비회원 조회 시 false입니다.")
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
                                      "profileImageUrl": "https://cdn.example.com/profile/2.jpg",
                                      "isCreator": true
                                    },
                                    "likeCount": 3,
                                    "isLiked": true,
                                    "images": [
                                      {
                                        "feelingReplyImageId": 1,
                                        "imageUrl": "https://cdn.example.com/artwork-feeling-replies/1.jpg",
                                        "width": 1200,
                                        "height": 800,
                                        "sortOrder": 0
                                      }
                                    ]
                                  }
                                ],
                                "nextCursorId": null,
                                "size": 10,
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
      @Parameter(description = "한 번에 불러올 답글 개수", example = "10")
          @RequestParam(defaultValue = "10")
          @Min(1) @Max(50) int size,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "작품 감상평 작성",
      description =
          "로그인 사용자가 감상평을 작성합니다. 해당 작품의 작가도 작성할 수 있으며, 내용은 공백이 아닌 1자 이상 300자 이하로 입력해야 합니다.")
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
                                "createdAt": "2026-06-30T22:10:00",
                                "images": [
                                  {
                                    "feelingImageId": 1,
                                    "imageUrl": "https://cdn.example.com/feeling-1.jpg",
                                    "width": 1200,
                                    "height": 900,
                                    "sortOrder": 0
                                  }
                                ]
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
      responseCode = "400",
      description = "감상평 내용 또는 이미지 검증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Invalid artwork feeling image",
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
                                  "field": "images[0].width",
                                  "message": "0보다 커야 합니다"
                                }
                              ]
                            },
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
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<ArtworkFeelingResponse> createFeeling(
      @Parameter(description = "감상평을 작성할 작품 ID", example = "1") Long artworkId,
      @Parameter(hidden = true) AuthUser user,
      @Valid CreateArtworkFeelingRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "작품 감상평 답변 등록",
      description =
          "작품 감상평에 대한 답변을 작성합니다. 작가와 일반 회원 모두 작성할 수 있으며, 내용은 공백이 아닌 1자 이상 300자 이하이고 이미지는 최대 5장까지 첨부할 수 있습니다.")
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
                                "nickname": "고상준",
                                "images": [
                                  {
                                    "feelingReplyImageId": 1,
                                    "imageUrl": "https://cdn.example.com/artwork-feeling-replies/1.jpg",
                                    "width": 1200,
                                    "height": 800,
                                    "sortOrder": 0
                                  }
                                ]
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
      description = "답변 내용 또는 이미지 검증 실패",
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
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<ArtworkFeelingReplyResponse> createFeelingReply(
      @Parameter(description = "감상평이 속한 작품 ID", example = "1") Long artworkId,
      @Parameter(description = "답변을 작성할 감상평 ID", example = "1") Long feelingId,
      @Parameter(hidden = true) AuthUser user,
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
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<DeletedArtworkFeelingReplyResponse> deleteFeelingReply(
      @Parameter(description = "감상평이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "답변이 속한 감상평 ID", example = "7") Long feelingId,
      @Parameter(description = "삭제할 감상평 답변 ID", example = "8") Long feelingReplyId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "작품 감상평 삭제",
      description =
          "사용자가 본인이 작성한 작품 감상평을 soft delete 방식으로 삭제합니다. 기존 답변은 삭제되지 않으며, 삭제된 감상평은 활성 답변이 남아 있는 동안 isDeleted=true 상태로 목록에 유지되고 마지막 활성 답변 삭제 후 목록에서 제외됩니다.")
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
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<DeletedArtworkFeelingResponse> deleteFeeling(
      @Parameter(description = "감상평이 속한 작품 ID", example = "1") Long artworkId,
      @Parameter(description = "삭제할 감상평 ID", example = "1") Long feelingId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "작품 감상평 좋아요 등록", description = "감상평 좋아요를 등록합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "감상평 좋아요 등록 성공",
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
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<ArtworkFeelingLikeResponse> feelingLike(
      @Parameter(description = "감상평이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "좋아요를 등록할 감상평 ID", example = "7") Long feelingId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "작품 감상평 답변 좋아요 등록", description = "감상평 답변 좋아요를 등록합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "감상평 답변 좋아요 등록 성공",
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
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<ArtworkFeelingReplyLikeResponse> feelingReplyLike(
      @Parameter(description = "감상평이 속한 작품 ID", example = "3") Long artworkId,
      @Parameter(description = "답변이 속한 감상평 ID", example = "7") Long feelingId,
      @Parameter(description = "좋아요를 등록할 감상평 답변 ID", example = "8") Long feelingReplyId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);
}
