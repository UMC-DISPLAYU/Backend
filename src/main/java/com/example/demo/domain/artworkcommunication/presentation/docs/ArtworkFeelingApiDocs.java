package com.example.demo.domain.artworkcommunication.presentation.docs;

import com.example.demo.domain.artworkcommunication.presentation.request.CreateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.request.UpdateArtworkFeelingRequest;
import com.example.demo.domain.artworkcommunication.presentation.response.ArtworkFeelingResponse;
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

@Tag(name = "Artwork Feeling", description = "작품 감상평 API")
public interface ArtworkFeelingApiDocs {

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
      CreateArtworkFeelingRequest request,
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
      UpdateArtworkFeelingRequest request,
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
                              "data": null
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
  ApiResponseBody<Void> deleteFeeling(
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
