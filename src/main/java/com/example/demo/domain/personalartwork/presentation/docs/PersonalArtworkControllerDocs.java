package com.example.demo.domain.personalartwork.presentation.docs;

import com.example.demo.domain.personalartwork.presentation.request.PersonalArtworkRequest;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkLikeResponse;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkResponse;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkSummaryResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "PersonalArtwork", description = "개인 작품(작가 프로필 아카이브) API")
public interface PersonalArtworkControllerDocs {

  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "개인 작품 등록", description = "작가 프로필에 전시와 무관한 개인 작품을 등록합니다.")
  @ApiResponse(
      responseCode = "201",
      description = "개인 작품 등록 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Create personal artwork success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalArtworkId": 1,
                                "userId": 1,
                                "artworkName": "작은 정원",
                                "content": "개인 작업으로 제작한 설치 작품입니다.",
                                "type": "COMPLEX",
                                "productionYear": 2026,
                                "materialMedia": "Mixed media",
                                "size": "100 x 100 x 150 cm",
                                "point": "빛과 그림자의 변화",
                                "createdAt": "2026-08-04T09:00:00",
                                "images": [
                                  {
                                    "imageId": 1,
                                    "imageUrl": "https://cdn.displayu.com/personal-artworks/garden.png",
                                    "isThumbnail": true,
                                    "imageType": "MAIN",
                                    "sortOrder": 1,
                                    "caption": "대표 이미지",
                                    "width": 1200,
                                    "height": 1600
                                  }
                                ]
                              }
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/personal-artworks" }
                          }
                          """)))
  ApiResponseBody<PersonalArtworkResponse> createPersonalArtwork(
      @Valid @RequestBody PersonalArtworkRequest personalArtworkRequest,
      AuthUser user,
      HttpServletRequest request);

  @Operation(
      summary = "개인 작품 목록 조회",
      description = "특정 유저(작가 프로필)의 개인 작품 목록을 가볍게 조회합니다 (작품탭 카드용). 본인/타인 조회 공용입니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork list success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": [
                                {
                                  "personalArtworkId": 1,
                                  "artworkName": "작은 정원",
                                  "thumbnailUrl": "https://cdn.displayu.com/personal-artworks/garden.png",
                                  "type": "COMPLEX",
                                  "createdAt": "2026-08-04T09:00:00"
                                }
                              ]
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/personal-artworks" }
                          }
                          """)))
  ApiResponseBody<List<PersonalArtworkSummaryResponse>> getPersonalArtworks(
      @Parameter(description = "조회할 작가 프로필의 유저 ID", example = "1") @RequestParam Long userId,
      HttpServletRequest request);

  @Operation(
      summary = "개인 작품 단건 상세 조회",
      description =
          """
          개인 작품의 전체 필드를 조회합니다. 작가 프로필에서 타인의 작품도 열람할 수 있으며 비회원도 조회 가능합니다.
          본인 작품의 수정 화면에 기존 값을 채울 때도 같은 응답을 사용합니다.

          로그인한 경우 isLiked와 isArchived에 본인의 좋아요·저장 여부가 담기고, 비회원이면 모두 false로 내려갑니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 단건 상세 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Personal artwork detail success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalArtworkId": 1,
                                "userId": 1,
                                "artistName": "김마야",
                                "artworkName": "작은 정원",
                                "content": "개인 작업으로 제작한 설치 작품입니다.",
                                "type": "COMPLEX",
                                "productionYear": 2026,
                                "materialMedia": "Mixed media",
                                "size": "100 x 100 x 150 cm",
                                "point": "빛과 그림자의 변화",
                                "createdAt": "2026-08-04T09:00:00",
                                "images": [
                                  {
                                    "imageId": 1,
                                    "imageUrl": "https://cdn.displayu.com/personal-artworks/garden.png",
                                    "isThumbnail": true,
                                    "imageType": "MAIN",
                                    "sortOrder": 1,
                                    "caption": "대표 이미지",
                                    "width": 1200,
                                    "height": 1600
                                  }
                                ],
                                "likeCount": 12,
                                "isLiked": true,
                                "isArchived": false
                              }
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/personal-artworks/1" }
                          }
                          """)))
  ApiResponseBody<PersonalArtworkResponse> getPersonalArtworkDetail(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      AuthUser user,
      HttpServletRequest request);

  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "개인 작품 수정", description = "본인이 등록한 개인 작품의 내용과 이미지를 수정합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 수정 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Update personal artwork success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalArtworkId": 1,
                                "userId": 1,
                                "artworkName": "작은 정원",
                                "content": "개인 작업으로 제작한 설치 작품입니다.",
                                "type": "COMPLEX",
                                "productionYear": 2026,
                                "materialMedia": "Mixed media",
                                "size": "100 x 100 x 150 cm",
                                "point": "빛과 그림자의 변화",
                                "createdAt": "2026-08-04T09:00:00",
                                "images": [
                                  {
                                    "imageId": 1,
                                    "imageUrl": "https://cdn.displayu.com/personal-artworks/garden.png",
                                    "isThumbnail": true,
                                    "imageType": "MAIN",
                                    "sortOrder": 1,
                                    "caption": "대표 이미지",
                                    "width": 1200,
                                    "height": 1600
                                  }
                                ]
                              }
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/personal-artworks/1" }
                          }
                          """)))
  ApiResponseBody<PersonalArtworkResponse> updatePersonalArtwork(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      @Valid @RequestBody PersonalArtworkRequest personalArtworkRequest,
      AuthUser user,
      HttpServletRequest request);

  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "개인 작품 삭제", description = "본인이 등록한 개인 작품을 삭제합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 삭제 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Delete personal artwork success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": null
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/personal-artworks/1" }
                          }
                          """)))
  ApiResponseBody<Void> deletePersonalArtwork(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      AuthUser user,
      HttpServletRequest request);

  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "개인 작품 좋아요 등록", description = "개인 작품에 좋아요를 등록합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 좋아요 등록 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Like personal artwork success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalArtworkId": 1,
                                "isLiked": true,
                                "likeCount": 8
                              }
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/personal-artworks/1/like" }
                          }
                          """)))
  ApiResponseBody<PersonalArtworkLikeResponse> likePersonalArtwork(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      AuthUser user,
      HttpServletRequest request);

  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "개인 작품 좋아요 취소", description = "개인 작품 좋아요를 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "개인 작품 좋아요 취소 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Cancel personal artwork like success",
                      value =
                          """
                          {
                            "resultType": "SUCCESS",
                            "success": {
                              "data": {
                                "personalArtworkId": 1,
                                "isLiked": false,
                                "likeCount": 7
                              }
                            },
                            "error": null,
                            "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/personal-artworks/1/like" }
                          }
                          """)))
  ApiResponseBody<PersonalArtworkLikeResponse> cancelPersonalArtworkLike(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      AuthUser user,
      HttpServletRequest request);
}
