package com.example.demo.domain.displayartwork.presentation;

import com.example.demo.domain.displayartwork.application.command.CreateDisplayArtworkService;
import com.example.demo.domain.displayartwork.application.command.DisplayArtworkLikeCommand;
import com.example.demo.domain.displayartwork.application.command.DisplayArtworkLikeCommandService;
import com.example.demo.domain.displayartwork.application.query.DisplayArtworkQueryService;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkDetailResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkLikeResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkPreviewResult;
import com.example.demo.domain.displayartwork.application.result.DisplayArtworkResult;
import com.example.demo.domain.displayartwork.domain.type.ArtworkType;
import com.example.demo.domain.displayartwork.domain.type.PreviewFilterType;
import com.example.demo.domain.displayartwork.presentation.mapper.DisplayArtworkPresentationMapper;
import com.example.demo.domain.displayartwork.presentation.request.CreateDisplayArtworkRequest;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkDetailResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkLikeResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkPreviewResponse;
import com.example.demo.domain.displayartwork.presentation.response.DisplayArtworkResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "DisplayArtwork", description = "전시 출품작 API")
public class DisplayArtworkController {

  // TODO: 인증 붙기 전까지 사용하는 임시 사용자 ID. 로그인 구현되면 인증 정보에서 꺼내오도록 교체해야 함.
  private static final Long TEMP_USER_ID = 1L;

  private final CreateDisplayArtworkService createDisplayArtworkService;
  private final DisplayArtworkQueryService displayArtworkQueryService;
  private final DisplayArtworkLikeCommandService displayArtworkLikeCommandService;
  private final DisplayArtworkPresentationMapper mapper;

  public DisplayArtworkController(
      CreateDisplayArtworkService createDisplayArtworkService,
      DisplayArtworkQueryService displayArtworkQueryService,
      DisplayArtworkLikeCommandService displayArtworkLikeCommandService,
      DisplayArtworkPresentationMapper mapper) {
    this.createDisplayArtworkService = createDisplayArtworkService;
    this.displayArtworkQueryService = displayArtworkQueryService;
    this.displayArtworkLikeCommandService = displayArtworkLikeCommandService;
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

  @PostMapping("/api/v1/artworks")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "전시 출품작 등록", description = "전시 팀원이 전시에 작품을 등록합니다.")
  public ApiResponseBody<DisplayArtworkResponse> createDisplayArtwork(
      @Valid @RequestBody CreateDisplayArtworkRequest request, HttpServletRequest httpRequest) {
    Long displayArtworkId =
        createDisplayArtworkService.createDisplayArtwork(TEMP_USER_ID, request.toCommand());
    DisplayArtworkResult result =
        displayArtworkQueryService.getDisplayArtworkDetail(displayArtworkId);
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @GetMapping("/api/v1/artworks/{artworkId}")
  @Operation(summary = "전시 출품작 상세 조회", description = "작품 소개 탭에 필요한 상세 정보를 조회합니다. 비회원도 조회 가능합니다.")
  public ApiResponseBody<DisplayArtworkDetailResponse> getDisplayArtworkFullDetail(
      @Parameter(description = "전시 출품작 ID", example = "1") @PathVariable Long artworkId,
      HttpServletRequest httpRequest) {
    DisplayArtworkDetailResult result =
        displayArtworkQueryService.getDisplayArtworkFullDetail(artworkId, TEMP_USER_ID);
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PostMapping("/api/v1/artworks/{artworkId}/like")
  @Operation(summary = "작품 좋아요 등록", description = "전시 출품작에 좋아요를 등록합니다.")
  public ApiResponseBody<DisplayArtworkLikeResponse> likeDisplayArtwork(
      @Parameter(description = "전시 출품작 ID", example = "1") @PathVariable Long artworkId,
      HttpServletRequest httpRequest) {
    DisplayArtworkLikeResult result =
        displayArtworkLikeCommandService.like(
            new DisplayArtworkLikeCommand(artworkId, TEMP_USER_ID));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping("/api/v1/artworks/{artworkId}/like")
  @Operation(summary = "작품 좋아요 취소", description = "전시 출품작 좋아요를 취소합니다.")
  public ApiResponseBody<DisplayArtworkLikeResponse> cancelDisplayArtworkLike(
      @Parameter(description = "전시 출품작 ID", example = "1") @PathVariable Long artworkId,
      HttpServletRequest httpRequest) {
    DisplayArtworkLikeResult result =
        displayArtworkLikeCommandService.cancel(
            new DisplayArtworkLikeCommand(artworkId, TEMP_USER_ID));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }
}
