package com.example.demo.domain.personalartwork.presentation;

import com.example.demo.domain.personalartwork.application.command.PersonalArtworkCommandService;
import com.example.demo.domain.personalartwork.application.command.PersonalArtworkLikeCommand;
import com.example.demo.domain.personalartwork.application.command.PersonalArtworkLikeCommandService;
import com.example.demo.domain.personalartwork.application.query.PersonalArtworkQueryService;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkLikeResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkResult;
import com.example.demo.domain.personalartwork.application.result.PersonalArtworkSummaryResult;
import com.example.demo.domain.personalartwork.presentation.mapper.PersonalArtworkPresentationMapper;
import com.example.demo.domain.personalartwork.presentation.request.PersonalArtworkRequest;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkLikeResponse;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkResponse;
import com.example.demo.domain.personalartwork.presentation.response.PersonalArtworkSummaryResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "PersonalArtwork", description = "개인 작품(작가 프로필 아카이브) API")
public class PersonalArtworkController {

  private final PersonalArtworkCommandService personalArtworkCommandService;
  private final PersonalArtworkQueryService personalArtworkQueryService;
  private final PersonalArtworkLikeCommandService personalArtworkLikeCommandService;
  private final PersonalArtworkPresentationMapper mapper;

  public PersonalArtworkController(
      PersonalArtworkCommandService personalArtworkCommandService,
      PersonalArtworkQueryService personalArtworkQueryService,
      PersonalArtworkLikeCommandService personalArtworkLikeCommandService,
      PersonalArtworkPresentationMapper mapper) {
    this.personalArtworkCommandService = personalArtworkCommandService;
    this.personalArtworkQueryService = personalArtworkQueryService;
    this.personalArtworkLikeCommandService = personalArtworkLikeCommandService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/personal-artworks")
  @ResponseStatus(HttpStatus.CREATED)
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
                      value = CREATE_ARTWORK_SUCCESS_EXAMPLE)))
  public ApiResponseBody<PersonalArtworkResponse> createPersonalArtwork(
      @Valid @RequestBody PersonalArtworkRequest personalArtworkRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    Long personalArtworkId =
        personalArtworkCommandService.createPersonalArtwork(
            requireUserId(user), personalArtworkRequest.toCommand());
    PersonalArtworkResult result =
        personalArtworkQueryService.getPersonalArtworkDetail(
            personalArtworkId, requireUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/personal-artworks")
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
                      value = ARTWORK_LIST_SUCCESS_EXAMPLE)))
  public ApiResponseBody<List<PersonalArtworkSummaryResponse>> getPersonalArtworks(
      @Parameter(description = "조회할 작가 프로필의 유저 ID", example = "1") @RequestParam Long userId,
      HttpServletRequest request) {
    List<PersonalArtworkSummaryResult> results =
        personalArtworkQueryService.getPersonalArtworksByUser(userId);
    return ApiResponseBody.success(results.stream().map(mapper::toResponse).toList(), request);
  }

  @GetMapping("/api/v1/personal-artworks/{personalArtworkId}")
  @Operation(
      summary = "개인 작품 단건 상세 조회",
      description =
          """
          개인 작품의 전체 필드를 조회합니다. 작가 프로필에서 타인의 작품도 열람할 수 있으며 비회원도 조회 가능합니다.
          본인 작품의 수정 화면에 기존 값을 채울 때도 같은 응답을 사용합니다.

          로그인한 경우 isLiked에 본인의 좋아요 여부가 담기고, 비회원이면 false로 내려갑니다.
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
                      value = ARTWORK_SUCCESS_EXAMPLE)))
  public ApiResponseBody<PersonalArtworkResponse> getPersonalArtworkDetail(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    PersonalArtworkResult result =
        personalArtworkQueryService.getPersonalArtworkDetail(
            personalArtworkId, optionalUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PatchMapping("/api/v1/personal-artworks/{personalArtworkId}")
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
                      value = ARTWORK_SUCCESS_EXAMPLE)))
  public ApiResponseBody<PersonalArtworkResponse> updatePersonalArtwork(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      @Valid @RequestBody PersonalArtworkRequest personalArtworkRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    personalArtworkCommandService.updatePersonalArtwork(
        personalArtworkId, requireUserId(user), personalArtworkRequest.toCommand());
    PersonalArtworkResult result =
        personalArtworkQueryService.getPersonalArtworkDetail(
            personalArtworkId, requireUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/personal-artworks/{personalArtworkId}")
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
                      value = VOID_SUCCESS_EXAMPLE)))
  public ApiResponseBody<Void> deletePersonalArtwork(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    personalArtworkCommandService.deletePersonalArtwork(personalArtworkId, requireUserId(user));
    return ApiResponseBody.success(null, request);
  }

  @PostMapping("/api/v1/personal-artworks/{personalArtworkId}/like")
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
                      value = LIKE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<PersonalArtworkLikeResponse> likePersonalArtwork(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    PersonalArtworkLikeResult result =
        personalArtworkLikeCommandService.like(
            new PersonalArtworkLikeCommand(personalArtworkId, requireUserId(user)));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/personal-artworks/{personalArtworkId}/like")
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
                      value = UNLIKE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<PersonalArtworkLikeResponse> cancelPersonalArtworkLike(
      @Parameter(description = "개인 작품 ID", example = "1") @PathVariable Long personalArtworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    PersonalArtworkLikeResult result =
        personalArtworkLikeCommandService.cancel(
            new PersonalArtworkLikeCommand(personalArtworkId, requireUserId(user)));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  // 인증이 필수인 API에서 사용한다. SecurityConfig가 모든 요청을 permitAll로 통과시키므로
  // 토큰이 없거나 유효하지 않으면 AuthUser가 null로 주입될 수 있어 컨트롤러단에서 막는다.
  private Long optionalUserId(AuthUser user) {
    return user == null ? null : user.userId();
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }

  private static final String ARTWORK_SUCCESS_EXAMPLE =
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
      """;

  private static final String CREATE_ARTWORK_SUCCESS_EXAMPLE =
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
      """;

  private static final String ARTWORK_LIST_SUCCESS_EXAMPLE =
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
      """;

  private static final String LIKE_SUCCESS_EXAMPLE =
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
      """;

  private static final String UNLIKE_SUCCESS_EXAMPLE =
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
      """;

  private static final String VOID_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": null
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/personal-artworks/1" }
      }
      """;
}
