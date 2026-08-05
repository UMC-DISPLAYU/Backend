package com.example.demo.domain.displayartwork.presentation;

import com.example.demo.domain.displayartwork.application.command.CreateDisplayArtworkService;
import com.example.demo.domain.displayartwork.application.command.DeleteDisplayArtworkService;
import com.example.demo.domain.displayartwork.application.command.DisplayArtworkLikeCommand;
import com.example.demo.domain.displayartwork.application.command.DisplayArtworkLikeCommandService;
import com.example.demo.domain.displayartwork.application.command.ReorderDisplayArtworksService;
import com.example.demo.domain.displayartwork.application.command.UpdateDisplayArtworkService;
import com.example.demo.domain.displayartwork.application.query.DisplayArtworkQueryService;
import com.example.demo.domain.displayartwork.application.result.DeleteDisplayArtworkResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkByArtistResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkDetailResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkEditResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkLikeResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkListResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkPreviewResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkResult;
import com.example.demo.domain.displayartwork.application.result.ReorderDisplayArtworksResult;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.PreviewFilterType;
import com.example.demo.domain.displayartwork.presentation.mapper.DisplayArtworkPresentationMapper;
import com.example.demo.domain.displayartwork.presentation.request.CreateDisplayArtworkRequest;
import com.example.demo.domain.displayartwork.presentation.request.ReorderDisplayArtworksRequest;
import com.example.demo.domain.displayartwork.presentation.request.UpdateDisplayArtworkRequest;
import com.example.demo.domain.displayartwork.presentation.response.DeleteDisplayArtworkResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkByArtistResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkDetailResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkEditResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkLikeResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkListResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkPreviewResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkResponse;
import com.example.demo.domain.displayartwork.presentation.response.ReorderDisplayArtworksResponse;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "DisplayArtwork", description = "전시 출품작 API")
public class DisplayArtworkController {

  private final CreateDisplayArtworkService createDisplayArtworkService;
  private final DisplayArtworkQueryService displayArtworkQueryService;
  private final DisplayArtworkLikeCommandService displayArtworkLikeCommandService;
  private final ReorderDisplayArtworksService reorderDisplayArtworksService;
  private final DeleteDisplayArtworkService deleteDisplayArtworkService;
  private final UpdateDisplayArtworkService updateDisplayArtworkService;
  private final DisplayArtworkPresentationMapper mapper;

  public DisplayArtworkController(
      CreateDisplayArtworkService createDisplayArtworkService,
      DisplayArtworkQueryService displayArtworkQueryService,
      DisplayArtworkLikeCommandService displayArtworkLikeCommandService,
      ReorderDisplayArtworksService reorderDisplayArtworksService,
      DeleteDisplayArtworkService deleteDisplayArtworkService,
      UpdateDisplayArtworkService updateDisplayArtworkService,
      DisplayArtworkPresentationMapper mapper) {
    this.createDisplayArtworkService = createDisplayArtworkService;
    this.displayArtworkQueryService = displayArtworkQueryService;
    this.displayArtworkLikeCommandService = displayArtworkLikeCommandService;
    this.reorderDisplayArtworksService = reorderDisplayArtworksService;
    this.deleteDisplayArtworkService = deleteDisplayArtworkService;
    this.updateDisplayArtworkService = updateDisplayArtworkService;
    this.mapper = mapper;
  }

  @GetMapping("/api/v1/artworks/preview")
  @Operation(
      summary = "홈 화면 - 작품 미리보기 목록 조회",
      description = "더보기 페이징을 포함한 작품 미리보기 카드 목록을 조회합니다. 비회원도 조회 가능합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 미리보기 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "Preview success", value = PREVIEW_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayArtworkPreviewResponse> getPreview(
      @Parameter(description = "대분류 필터 (RECOMMEND: 추천 탭, GRADUATION: 2025 졸작 탭)") @RequestParam
          PreviewFilterType type,
      @Parameter(description = "하위 분야 필터") @RequestParam(required = false) ArtworkType field,
      @Parameter(description = "하위 대학교 필터") @RequestParam(required = false) String school,
      @Parameter(description = "요청할 페이지 번호 (0부터 시작)") @RequestParam @PositiveOrZero int page,
      @Parameter(description = "한 번에 가져올 작품 개수") @RequestParam @Min(1) @Max(50) int size,
      HttpServletRequest httpRequest) {
    DisplayArtworkPreviewResult result =
        displayArtworkQueryService.getPreview(type, field, school, page, size);
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @GetMapping(
      value = "/api/v1/artworks",
      params = {"displayId", "!userId"})
  @Operation(
      summary = "전시 상세 - 작품 탭 목록 조회",
      description =
          """
          특정 전시(displayId)에 등록된 작품 전체를 전시 내 순서(workSortOrder)대로 조회합니다. 비회원도 조회 가능합니다.

          이 경로는 displayId와 userId 중 **정확히 하나만** 받습니다.
          - `?displayId={displayId}` : 전시 상세의 작품 탭 (이 API)
          - `?userId={userId}` : 작가 프로필의 작품 탭 (아래 별도 설명 참고)

          둘 다 넣거나 둘 다 빼면 400을 반환합니다.
          Swagger UI에서는 두 API가 한 항목으로 합쳐져 보이므로, 사용할 파라미터 하나만 채우고 나머지는 비워두세요.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "전시별 작품 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Display artworks success",
                      value = DISPLAY_ARTWORKS_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayArtworkListResponse> getArtworksByDisplay(
      @Parameter(description = "조회할 전시 ID (userId와 함께 쓸 수 없음)")
          @RequestParam(required = false)
          @Positive Long displayId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkListResult result =
        displayArtworkQueryService.getArtworksByDisplayId(displayId, optionalUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @GetMapping(
      value = "/api/v1/artworks",
      params = {"userId", "!displayId"})
  @Operation(
      summary = "작가 프로필 - 작품 탭 목록 조회",
      description =
          """
          해당 유저가 참여한 전시 출품작을 등록순(createdAt)으로 조회합니다.
          대표 작가와 공동 작업자를 구분하지 않고 모두 포함하며, 비회원도 조회 가능합니다.

          `?userId={userId}` 만 넣어서 호출합니다. displayId와 함께 쓸 수 없습니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "작가별 작품 목록 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artist artworks success",
                      value = ARTIST_ARTWORKS_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayArtworkByArtistResponse> getArtworksByArtist(
      @Parameter(description = "조회할 작가(유저) ID (displayId와 함께 쓸 수 없음)")
          @RequestParam(required = false)
          @Positive Long userId,
      HttpServletRequest httpRequest) {
    DisplayArtworkByArtistResult result = displayArtworkQueryService.getArtworksByUserId(userId);
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PostMapping("/api/v1/artworks")
  @ResponseStatus(HttpStatus.CREATED)
  @SecurityRequirement(name = "Authorization")
  @Operation(
      summary = "전시 출품작 등록",
      description =
          """
          전시 팀원이 작품 정보와 대표 작가/공동 작업자/내부 Q&A 담당자를 한 번에 등록합니다.
          내부 Q&A 담당자(qaHandlerUserIds)는 최소 1명이 필요하며 여러 명을 지정할 수 있습니다.
          담당자는 대표 작가, 계정이 연결된 공동 작업자, 전시 대표자 중에서만 지정할 수 있습니다.
          """)
  @ApiResponse(
      responseCode = "201",
      description = "전시 출품작 등록 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "Create artwork success", value = ARTWORK_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayArtworkResponse> createDisplayArtwork(
      @Valid @RequestBody CreateDisplayArtworkRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkResult result =
        createDisplayArtworkService.createDisplayArtwork(requireUserId(user), request.toCommand());
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PutMapping("/api/v1/artworks/order")
  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "전시 출품작 노출 순서 편집", description = "전시 대표자가 드래그 앤 드롭으로 변경한 작품 순서를 저장합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "전시 출품작 노출 순서 편집 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Reorder artworks success",
                      value = REORDER_SUCCESS_EXAMPLE)))
  public ApiResponseBody<ReorderDisplayArtworksResponse> reorderDisplayArtworks(
      @Valid @RequestBody ReorderDisplayArtworksRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    ReorderDisplayArtworksResult result =
        reorderDisplayArtworksService.reorder(requireUserId(user), request.toCommand());
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @GetMapping("/api/v1/artworks/{artworkId}")
  @Operation(
      summary = "전시 출품작 상세 조회",
      description =
          "작품 소개 탭에 필요한 상세 정보를 조회합니다. 비회원도 조회 가능하며, 로그인한 경우에만 isLiked/isSaved가 사용자 기준으로 계산됩니다.")
  @ApiResponse(
      responseCode = "200",
      description = "전시 출품작 상세 조회 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Artwork detail success",
                      value = ARTWORK_DETAIL_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayArtworkDetailResponse> getDisplayArtworkFullDetail(
      @Parameter(description = "전시 출품작 ID", example = "1") @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkDetailResult result =
        displayArtworkQueryService.getDisplayArtworkFullDetail(artworkId, optionalUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @GetMapping("/api/v1/artworks/{artworkId}/edit")
  @SecurityRequirement(name = "Authorization")
  @Operation(
      summary = "전시 출품작 수정 화면 조회",
      description =
          """
          수정 화면 진입 시 등록 당시 상태를 그대로 복원하기 위한 전체 필드를 조회합니다.
          관람객용 상세 조회와 달리 공동 작업자 목록과 Q&A 담당자 ID를 포함합니다.
          전시 대표자, 작품의 작가, 공동 작업자만 조회할 수 있습니다.
          """)
  public ApiResponseBody<DisplayArtworkEditResponse> getDisplayArtworkForEdit(
      @Parameter(description = "조회할 작품 ID") @PathVariable @Positive Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkEditResult result =
        displayArtworkQueryService.getArtworkForEdit(artworkId, requireUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PatchMapping("/api/v1/artworks/{artworkId}")
  @SecurityRequirement(name = "Authorization")
  @Operation(
      summary = "전시 출품작 수정",
      description =
          """
          작품 정보, 이미지, 작가 정보(대표 작가/공동 작업자/Q&A 담당자)를 수정합니다.
          전시 대표자, 작품의 작가, 공동 작업자만 수정할 수 있습니다.

          이미지는 화면에서 최종적으로 남은 목록을 그대로 보내면 그 상태로 저장됩니다.
          작품 이미지(ARTWORK) 최소 1장이 필요합니다.
          """)
  public ApiResponseBody<DisplayArtworkResponse> updateDisplayArtwork(
      @Parameter(description = "수정할 작품 ID") @PathVariable @Positive Long artworkId,
      @Valid @RequestBody UpdateDisplayArtworkRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkResult result =
        updateDisplayArtworkService.update(
            requireUserId(user), mapper.toCommand(artworkId, request));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping("/api/v1/artworks/{artworkId}")
  @SecurityRequirement(name = "Authorization")
  @Operation(
      summary = "전시 출품작 삭제",
      description = "전시 대표자는 팀원의 작품도 강제 삭제할 수 있고, 등록자는 본인이 등록한 작품만 삭제할 수 있습니다.")
  @ApiResponse(
      responseCode = "200",
      description = "전시 출품작 삭제 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "Delete artwork success", value = DELETE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DeleteDisplayArtworkResponse> deleteDisplayArtwork(
      @Parameter(description = "전시 출품작 ID", example = "1") @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DeleteDisplayArtworkResult result =
        deleteDisplayArtworkService.delete(requireUserId(user), artworkId);
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PostMapping("/api/v1/artworks/{artworkId}/like")
  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "작품 좋아요 등록", description = "전시 출품작에 좋아요를 등록합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 좋아요 등록 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "Like artwork success", value = LIKE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayArtworkLikeResponse> likeDisplayArtwork(
      @Parameter(description = "전시 출품작 ID", example = "1") @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkLikeResult result =
        displayArtworkLikeCommandService.like(
            new DisplayArtworkLikeCommand(artworkId, requireUserId(user)));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping("/api/v1/artworks/{artworkId}/like")
  @SecurityRequirement(name = "Authorization")
  @Operation(summary = "작품 좋아요 취소", description = "전시 출품작 좋아요를 취소합니다.")
  @ApiResponse(
      responseCode = "200",
      description = "작품 좋아요 취소 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "Cancel artwork like success",
                      value = UNLIKE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayArtworkLikeResponse> cancelDisplayArtworkLike(
      @Parameter(description = "전시 출품작 ID", example = "1") @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkLikeResult result =
        displayArtworkLikeCommandService.cancel(
            new DisplayArtworkLikeCommand(artworkId, requireUserId(user)));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  // 인증이 필수인 API에서 사용한다. SecurityConfig가 모든 요청을 permitAll로 통과시키므로
  // 토큰이 없거나 유효하지 않으면 AuthUser가 null로 주입될 수 있어 컨트롤러단에서 막는다.
  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }

  // 비회원도 호출 가능한 API에서 사용한다. 로그인하지 않았으면 null을 그대로 전달한다.
  private Long optionalUserId(AuthUser user) {
    return user == null ? null : user.userId();
  }

  private static final String PREVIEW_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artworks": [
              {
                "artworkId": 1,
                "artworkName": "Blue Moment",
                "artistName": "김마야",
                "artworkImageUrl": "https://cdn.displayu.com/artworks/blue.png",
                "imageWidth": 1200,
                "imageHeight": 1600,
                "exhibitionInfo": {
                  "displayId": 1,
                  "exhibitionTitle": "FORM 2026",
                  "exhibitionPeriod": "2026.05.28 - 2026.06.05",
                  "exhibitionLocation": "디유 갤러리"
                }
              }
            ],
            "page": 0,
            "size": 10,
            "isLast": true
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/artworks/preview" }
      }
      """;

  private static final String DISPLAY_ARTWORKS_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artworks": [
              {
                "artworkId": 1,
                "artworkName": "Blue Moment",
                "artistName": "김마야",
                "artworkImageUrl": "https://cdn.displayu.com/artworks/blue.png",
                "imageWidth": 1200,
                "imageHeight": 1600
              }
            ]
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/artworks" }
      }
      """;

  private static final String ARTIST_ARTWORKS_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artworks": [
              {
                "artworkId": 1,
                "artworkName": "Blue Moment",
                "artistName": "김마야",
                "artworkImageUrl": "https://cdn.displayu.com/artworks/blue.png",
                "imageWidth": 1200,
                "imageHeight": 1600,
                "createdAt": "2026-08-04T09:00:00",
                "exhibitionInfo": {
                  "displayId": 1,
                  "exhibitionTitle": "FORM 2026",
                  "exhibitionPeriod": "2026.05.28 - 2026.06.05",
                  "exhibitionLocation": "디유 갤러리"
                }
              }
            ]
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/artworks" }
      }
      """;

  private static final String ARTWORK_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artworkId": 1,
            "displayId": 1,
            "artworkName": "Blue Moment",
            "content": "푸른 시간의 밀도를 기록한 작업입니다.",
            "type": "PAINTING",
            "productionYear": 2026,
            "materialMedia": "Oil on canvas",
            "size": "72.7 x 90.9 cm",
            "point": "겹겹이 쌓인 색의 레이어",
            "workSortOrder": 1,
            "images": [
              {
                "imageId": 1,
                "imageUrl": "https://cdn.displayu.com/artworks/blue.png",
                "isThumbnail": true,
                "imageType": "MAIN",
                "sortOrder": 1,
                "caption": "대표 이미지",
                "width": 1200,
                "height": 1600
              }
            ],
            "artistName": "김마야",
            "artistUserId": 1,
            "coAuthorCount": 0,
            "qaHandlerUserIds": [1]
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/artworks" }
      }
      """;

  private static final String REORDER_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "displayId": 1,
            "updatedCount": 3
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/artworks/order" }
      }
      """;

  private static final String ARTWORK_DETAIL_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artworkId": 1,
            "artworkName": "Blue Moment",
            "content": "푸른 시간의 밀도를 기록한 작업입니다.",
            "type": "PAINTING",
            "productionYear": 2026,
            "size": "72.7 x 90.9 cm",
            "materialMedia": "Oil on canvas",
            "point": "겹겹이 쌓인 색의 레이어",
            "images": [
              {
                "imageId": 1,
                "imageUrl": "https://cdn.displayu.com/artworks/blue.png",
                "isThumbnail": true,
                "imageType": "MAIN",
                "sortOrder": 1,
                "caption": "대표 이미지",
                "width": 1200,
                "height": 1600
              }
            ],
            "artistName": "김마야",
            "artistUserId": 1,
            "qaHandlers": [
              {
                "userId": 1,
                "name": "김마야"
              }
            ],
            "exhibitionInfo": {
              "displayId": 1,
              "exhibitionTitle": "FORM 2026",
              "exhibitionThumbnailUrl": "https://cdn.displayu.com/posters/form.png",
              "exhibitionOrganizer": "디유대학교",
              "exhibitionPeriod": "2026.05.28 - 2026.06.05",
              "exhibitionLocation": "디유 갤러리"
            },
            "likeCount": 12,
            "isLiked": true,
            "isSaved": false
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/artworks/1" }
      }
      """;

  private static final String DELETE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "deletedArtworkId": 1,
            "message": "전시 출품작이 삭제되었습니다."
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/artworks/1" }
      }
      """;

  private static final String LIKE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artworkId": 1,
            "isLiked": true,
            "likeCount": 12
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/artworks/1/like" }
      }
      """;

  private static final String UNLIKE_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "artworkId": 1,
            "isLiked": false,
            "likeCount": 11
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/artworks/1/like" }
      }
      """;
}
