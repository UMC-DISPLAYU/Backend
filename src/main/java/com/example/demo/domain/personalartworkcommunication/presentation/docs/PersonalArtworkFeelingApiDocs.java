package com.example.demo.domain.personalartworkcommunication.presentation.docs;

import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingReplyRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.request.CreatePersonalArtworkFeelingRequest;
import com.example.demo.domain.personalartworkcommunication.presentation.response.DeletedPersonalArtworkFeelingResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingReplyResponse;
import com.example.demo.domain.personalartworkcommunication.presentation.response.PersonalArtworkFeelingResponse;
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

@Tag(name = "Personal Artwork Feeling", description = "개인 작품 감상평 API")
public interface PersonalArtworkFeelingApiDocs {

  @Operation(
      summary = "개인 작품 감상평 답변 등록",
      description = "개인 작품의 감상평에 답변을 등록합니다. 개인 작품 소유 여부를 isCreator로 반환합니다.")
  @ApiResponse(responseCode = "200", description = "개인 작품 감상평 답변 등록 성공")
  @ApiResponse(responseCode = "404", description = "개인 작품, 감상평 또는 사용자 없음")
  ApiResponseBody<PersonalArtworkFeelingReplyResponse> createFeelingReply(
      @Parameter(description = "감상평이 속한 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "답변을 등록할 감상평 ID", example = "1") Long personalFeelingId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
      @Valid CreatePersonalArtworkFeelingReplyRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(summary = "개인 작품 감상평 작성", description = "개인 작품 상세 화면에서 사용자가 감상평을 작성합니다.")
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
                                "createdAt": "2026-07-20T22:20:00"
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
      description = "감상평 내용 검증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples =
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
                              "path": "/api/v1/personal-artworks/1/feelings"
                            }
                          }
                          """)))
  ApiResponseBody<PersonalArtworkFeelingResponse> createPersonalFeeling(
      @Parameter(description = "감상평을 작성할 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
      @Valid CreatePersonalArtworkFeelingRequest request,
      HttpServletRequest httpServletRequest);

  @Operation(
      summary = "개인 작품 감상평 삭제",
      description = "사용자가 본인이 작성한 개인 작품 감상평을 soft delete 방식으로 삭제합니다.")
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
  ApiResponseBody<DeletedPersonalArtworkFeelingResponse> deleteFeeling(
      @Parameter(description = "감상평이 속한 개인 작품 ID", example = "1") Long personalArtworkId,
      @Parameter(description = "삭제할 개인 작품 감상평 ID", example = "1") Long personalFeelingId,
      @Parameter(
              name = "X-User-Id",
              description = "인증 구현 전까지 사용하는 테스트용 사용자 ID",
              in = ParameterIn.HEADER,
              example = "1")
          Long userId,
      HttpServletRequest httpServletRequest);
}
