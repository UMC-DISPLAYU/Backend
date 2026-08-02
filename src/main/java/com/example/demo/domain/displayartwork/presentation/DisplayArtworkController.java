package com.example.demo.domain.displayartwork.presentation;

import com.example.demo.domain.displayartwork.application.command.CreateDisplayArtworkService;
import com.example.demo.domain.displayartwork.application.command.DeleteDisplayArtworkService;
import com.example.demo.domain.displayartwork.application.command.DisplayArtworkLikeCommand;
import com.example.demo.domain.displayartwork.application.command.DisplayArtworkLikeCommandService;
import com.example.demo.domain.displayartwork.application.command.ReorderDisplayArtworksService;
import com.example.demo.domain.displayartwork.application.query.DisplayArtworkQueryService;
import com.example.demo.domain.displayartwork.application.result.DeleteDisplayArtworkResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkDetailResult;
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
import com.example.demo.domain.displayartwork.presentation.response.DeleteDisplayArtworkResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkDetailResponse;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final DisplayArtworkPresentationMapper mapper;

  public DisplayArtworkController(
      CreateDisplayArtworkService createDisplayArtworkService,
      DisplayArtworkQueryService displayArtworkQueryService,
      DisplayArtworkLikeCommandService displayArtworkLikeCommandService,
      ReorderDisplayArtworksService reorderDisplayArtworksService,
      DeleteDisplayArtworkService deleteDisplayArtworkService,
      DisplayArtworkPresentationMapper mapper) {
    this.createDisplayArtworkService = createDisplayArtworkService;
    this.displayArtworkQueryService = displayArtworkQueryService;
    this.displayArtworkLikeCommandService = displayArtworkLikeCommandService;
    this.reorderDisplayArtworksService = reorderDisplayArtworksService;
    this.deleteDisplayArtworkService = deleteDisplayArtworkService;
    this.mapper = mapper;
  }

  @GetMapping("/api/v1/artworks/preview")
  @Operation(
      summary = "홈 화면 - 작품 미리보기 목록 조회",
      description = "더보기 페이징을 포함한 작품 미리보기 카드 목록을 조회합니다. 비회원도 조회 가능합니다.")
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

  @GetMapping("/api/v1/artworks")
  @Operation(
      summary = "전시 상세 - 작품 탭 목록 조회",
      description = "특정 전시(displayId)에 등록된 작품 전체를 전시 내 순서(workSortOrder)대로 조회합니다. 비회원도 조회 가능합니다.")
  public ApiResponseBody<DisplayArtworkListResponse> getArtworksByDisplay(
      @Parameter(description = "조회할 전시 ID") @RequestParam Long displayId,
      HttpServletRequest httpRequest) {
    DisplayArtworkListResult result = displayArtworkQueryService.getArtworksByDisplayId(displayId);
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
  public ApiResponseBody<DisplayArtworkDetailResponse> getDisplayArtworkFullDetail(
      @Parameter(description = "전시 출품작 ID", example = "1") @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkDetailResult result =
        displayArtworkQueryService.getDisplayArtworkFullDetail(artworkId, optionalUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping("/api/v1/artworks/{artworkId}")
  @SecurityRequirement(name = "Authorization")
  @Operation(
      summary = "전시 출품작 삭제",
      description = "전시 대표자는 팀원의 작품도 강제 삭제할 수 있고, 등록자는 본인이 등록한 작품만 삭제할 수 있습니다.")
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
}
