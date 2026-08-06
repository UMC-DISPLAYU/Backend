package com.example.demo.domain.personalartworkcommunication.presentation.docs;

import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkFeelingReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkFeelingResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingLikeResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingListResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingReplyLikeResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingReplyListResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingResponse;
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

@Tag(name = "Personal Artwork Feeling", description = "개인 작품 감상평 API")
public interface PersonalArtworkFeelingApiDocs {

  @Operation(
      summary = "개인 작품 감상평 목록 조회",
      description =
          "개인 작품에 등록된 감상평을 커서 방식으로 조회하며 작성자의 프로필 이미지 URL을 user.profileImageUrl로 반환합니다. 삭제된 감상평도 isDeleted=true 상태로 목록에 유지됩니다. 로그인 사용자의 좋아요 여부는 isLiked, 본인 작성 여부는 isMine으로 반환하며 비회원 조회 시 두 값은 false입니다. 답변은 별도 API에서 조회합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 감상평 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork feeling list success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "feelings": [
                                  {
                                    "personalFeelingId": 1,
                                    "content": "색감이 정말 인상적이에요.",
                                    "createdAt": "2026-07-23T16:00:00",
                                    "isDeleted": false,
                                    "isMine": true,
                                    "user": {
                                      "userId": 2,
                                      "nickname": "관람객",
                                      "profileImageUrl": "https://cdn.example.com/profile/2.jpg",
                                      "isCreator": false
                                    },
                                    "images": [
                                      {
                                        "personalFeelingImageId": 1,
                                        "imageUrl": "https://cdn.example.com/personal-feeling-1.jpg",
                                        "width": 1200,
                                        "height": 900,
                                        "sortOrder": 0
                                      }
                                    ],
                                    "likeCount": 4,
                                    "isLiked": true,
                                    "replyCount": 1
                                  }
                                ],
                                "nextCursorId": 1,
                                "size": 10,
                                "hasNext": true
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-23T16:20:00",
                              "path": "/api/v1/personal-artworks/1/feelings"
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
                              "timestamp": "2026-07-23T16:20:00",
                              "path": "/api/v1/personal-artworks/1/feelings"
                            }
                          }
                          """)))
  ApiResponseBody<PersonalArtworkFeelingListResponse> getFeelings(
      @Parameter(description = "감상평을 조회할 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "다음 페이지 조회를 위한 마지막 감상평 ID", example = "3") @Positive Long cursorId,
      @Parameter(description = "한 번에 불러올 감상평 개수", example = "10")
          @RequestParam(defaultValue = "10")
          @Min(1) @Max(50) int size,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "개인 작품 감상평 답변 목록 조회",
      description =
          "특정 감상평의 삭제되지 않은 답변을 커서 방식으로 조회하며 작성자의 프로필 이미지 URL을 user.profileImageUrl로 반환합니다. 원본 감상평이 삭제된 경우에도 기존 답변은 유지되고 조회할 수 있습니다. 로그인 사용자의 좋아요 여부는 isLiked로 반환하며, 비회원 조회 시 false입니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 감상평 답변 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork feeling reply list success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "replies": [
                                  {
                                    "personalFeelingReplyId": 1,
                                    "content": "감상해 주셔서 감사합니다.",
                                    "createdAt": "2026-07-23T16:10:00",
                                    "user": {
                                      "userId": 1,
                                      "nickname": "작품소유자",
                                      "profileImageUrl": "https://cdn.example.com/profile/1.jpg",
                                      "isCreator": true
                                    },
                                    "likeCount": 2,
                                    "isLiked": true
                                  }
                                ],
                                "nextCursorId": null,
                                "size": 10,
                                "hasNext": false
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-23T16:20:00",
                              "path": "/api/v1/personal-artworks/1/feelings/1/replies"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "404", description = "개인 작품 또는 감상평 없음")
  ApiResponseBody<PersonalArtworkFeelingReplyListResponse> getFeelingReplies(
      @Parameter(description = "감상평이 속한 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "답변을 조회할 감상평 ID", example = "1") Long personalFeelingId,
      @Parameter(description = "다음 페이지 조회를 위한 마지막 답변 ID", example = "3") @Positive Long cursorId,
      @Parameter(description = "한 번에 불러올 답글 개수", example = "10")
          @RequestParam(defaultValue = "10")
          @Min(1) @Max(50) int size,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "개인 작품 감상평 답변 등록",
      description = "개인 작품의 감상평에 답변을 등록합니다. 내용은 공백이 아닌 1자 이상 300자 이하로 입력해야 합니다.")
  @ApiResponse(responseCode = "200", description = "개인 작품 감상평 답변 등록 성공")
  @ApiResponse(responseCode = "401", description = "인증 필요")
  @ApiResponse(responseCode = "404", description = "개인 작품, 감상평 또는 사용자 없음")
  ApiResponseBody<PersonalArtworkFeelingReplyResponse> createFeelingReply(
      @Parameter(description = "감상평이 속한 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "답변을 등록할 감상평 ID", example = "1") Long personalFeelingId,
      @Parameter(hidden = true) AuthUser user,
      @Valid CreatePersonalArtworkFeelingReplyRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "개인 작품 감상평 답변 삭제", description = "작성자가 본인이 작성한 개인 작품 감상평 답변을 삭제합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 감상평 답변 삭제 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork feeling reply delete success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalFeelingReplyId": 1,
                                "deletedAt": "2026-07-26T04:15:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-26T04:15:00",
                              "path": "/api/v1/personal-artworks/1/feelings/1/reply/1"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "403", description = "감상평 답변 삭제 권한 없음")
  @ApiResponse(responseCode = "401", description = "인증 필요")
  @ApiResponse(responseCode = "404", description = "개인 작품, 사용자, 감상평 또는 감상평 답변 없음")
  ApiResponseBody<DeletedPersonalArtworkFeelingReplyResponse> deleteFeelingReply(
      @Parameter(description = "감상평이 속한 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "답변이 속한 감상평 ID", example = "1") Long personalFeelingId,
      @Parameter(description = "삭제할 감상평 답변 ID", example = "1") Long personalFeelingReplyId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "개인 작품 감상평 작성",
      description =
          "로그인 사용자가 감상평을 작성합니다. 해당 개인 작품의 소유자도 작성할 수 있으며, 내용은 공백이 아닌 1자 이상 300자 이하로 입력해야 합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 감상평 작성 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork feeling create success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalFeelingId": 1,
                                "userId": 1,
                                "content": "정말 감동적인 작품이에요.",
                                "createdAt": "2026-07-20T22:20:00",
                                "images": [
                                  {
                                    "personalFeelingImageId": 1,
                                    "imageUrl": "https://cdn.example.com/personal-feeling-1.jpg",
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
                              "path": "/api/v1/personal-artworks/1/feelings"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "400",
      description = "감상평 내용 또는 이미지 검증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(
                    name = "Invalid personal artwork feeling image",
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
                            "timestamp": "2026-07-20T22:20:00",
                            "path": "/api/v1/personal-artworks/1/feelings"
                          }
                        }
                        """),
                @ExampleObject(
                    name = "Invalid personal artwork feeling content",
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
                                  "message": "감상평 내용은 필수입니다."
                                }
                              ]
                            },
                            "meta": {
                              "timestamp": "2026-07-20T22:20:00",
                              "path": "/api/v1/personal-artworks/1/feelings"
                            }
                          }
                          """)
              }))
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
                              "path": "/api/v1/personal-artworks/1/feelings"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<PersonalArtworkFeelingResponse> createPersonalFeeling(
      @Parameter(description = "감상평을 작성할 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(hidden = true) AuthUser user,
      @Valid CreatePersonalArtworkFeelingRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "개인 작품 감상평 좋아요 등록 및 취소",
      description = "좋아요가 없거나 취소된 상태면 등록하고, 등록된 상태면 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 감상평 좋아요 상태 변경 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork feeling like toggle success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalFeelingId": 1,
                                "liked": true,
                                "likeCount": 3,
                                "createdAt": "2026-07-23T15:00:00",
                                "deletedAt": null
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-23T15:00:00",
                              "path": "/api/v1/personal-artworks/1/feelings/1/like"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "개인 작품, 감상평 또는 사용자 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork feeling like target not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "PERSONAL_FEELING_NOT_FOUND",
                              "message": "개인 작품 감상평을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-07-23T15:00:00",
                              "path": "/api/v1/personal-artworks/1/feelings/1/like"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<PersonalArtworkFeelingLikeResponse> feelingLike(
      @Parameter(description = "감상평이 속한 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "좋아요 상태를 변경할 감상평 ID", example = "1") Long personalFeelingId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "개인 작품 감상평 삭제",
      description =
          "사용자가 본인이 작성한 개인 작품 감상평을 soft delete 방식으로 삭제합니다. 감상평은 isDeleted=true 상태로 목록에 유지되며 기존 답변은 삭제되지 않습니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 감상평 삭제 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork feeling delete success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalFeelingId": 1,
                                "deletedAt": "2026-07-22T15:10:00"
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-22T15:10:00",
                              "path": "/api/v1/personal-artworks/1/feelings/1"
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
                      name = "Personal artwork feeling delete forbidden",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "PERSONAL_ARTWORK_FEELING_FORBIDDEN",
                              "message": "본인이 작성한 감상평만 삭제할 수 있습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-07-22T15:10:00",
                              "path": "/api/v1/personal-artworks/1/feelings/1"
                            }
                          }
                          """)))
  @ApiResponse(
      responseCode = "404",
      description = "개인 작품, 사용자 또는 감상평 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork feeling delete not found",
                      value =
                          """
                          {
                            "resultType": "FAIL",
                            "success": null,
                            "error": {
                              "code": "PERSONAL_FEELING_NOT_FOUND",
                              "message": "개인 작품 감상평을 찾을 수 없습니다.",
                              "details": null
                            },
                            "meta": {
                              "timestamp": "2026-07-22T15:10:00",
                              "path": "/api/v1/personal-artworks/1/feelings/1"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<DeletedPersonalArtworkFeelingResponse> deleteFeeling(
      @Parameter(description = "감상평이 속한 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "삭제할 개인 작품 감상평 ID", example = "1") Long personalFeelingId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "개인 작품 감상평 답변 좋아요 등록 및 취소",
      description = "좋아요가 없거나 취소된 상태면 등록하고, 등록된 상태면 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 감상평 답변 좋아요 상태 변경 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork feeling reply like toggle success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalFeelingReplyId": 1,
                                "liked": true,
                                "likeCount": 3,
                                "createdAt": "2026-07-26T04:10:00",
                                "deletedAt": null
                              }
                            },
                            "error": null,
                            "meta": {
                              "timestamp": "2026-07-26T04:10:00",
                              "path": "/api/v1/personal-artworks/1/feelings/1/reply/1/like"
                            }
                          }
                          """)))
  @ApiResponse(responseCode = "404", description = "개인 작품, 사용자, 감상평 또는 감상평 답변 없음")
  @ApiResponse(responseCode = "401", description = "인증 필요")
  ApiResponseBody<PersonalArtworkFeelingReplyLikeResponse> feelingReplyLike(
      @Parameter(description = "감상평이 속한 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "답변이 속한 감상평 ID", example = "1") Long personalFeelingId,
      @Parameter(description = "좋아요를 변경할 감상평 답변 ID", example = "1") Long personalFeelingReplyId,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpServletRequest);
}
