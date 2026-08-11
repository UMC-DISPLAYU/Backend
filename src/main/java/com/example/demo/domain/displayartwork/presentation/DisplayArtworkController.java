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
import com.example.demo.domain.displayartwork.presentation.docs.DisplayArtworkControllerDocs;
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
import jakarta.servlet.http.HttpServletRequest;
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
public class DisplayArtworkController implements DisplayArtworkControllerDocs {

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
  @Override
  public ApiResponseBody<DisplayArtworkPreviewResponse> getPreview(
      @RequestParam PreviewFilterType type,
      @RequestParam(required = false) ArtworkType field,
      @RequestParam(required = false) String school,
      @RequestParam int page,
      @RequestParam int size,
      HttpServletRequest httpRequest) {
    DisplayArtworkPreviewResult result =
        displayArtworkQueryService.getPreview(type, field, school, page, size);
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @GetMapping(
      value = "/api/v1/artworks",
      params = {"displayId", "!userId"})
  @Override
  public ApiResponseBody<DisplayArtworkListResponse> getArtworksByDisplay(
      @RequestParam(required = false) Long displayId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkListResult result =
        displayArtworkQueryService.getArtworksByDisplayId(displayId, optionalUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @GetMapping(
      value = "/api/v1/artworks",
      params = {"userId", "!displayId"})
  @Override
  public ApiResponseBody<DisplayArtworkByArtistResponse> getArtworksByArtist(
      @RequestParam(required = false) Long userId, HttpServletRequest httpRequest) {
    DisplayArtworkByArtistResult result = displayArtworkQueryService.getArtworksByUserId(userId);
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PostMapping("/api/v1/artworks")
  @ResponseStatus(HttpStatus.CREATED)
  @Override
  public ApiResponseBody<DisplayArtworkResponse> createDisplayArtwork(
      @RequestBody CreateDisplayArtworkRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkResult result =
        createDisplayArtworkService.createDisplayArtwork(
            requireUserId(user), mapper.toCommand(request));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PutMapping("/api/v1/artworks/order")
  @Override
  public ApiResponseBody<ReorderDisplayArtworksResponse> reorderDisplayArtworks(
      @RequestBody ReorderDisplayArtworksRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    ReorderDisplayArtworksResult result =
        reorderDisplayArtworksService.reorder(requireUserId(user), mapper.toCommand(request));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @GetMapping("/api/v1/artworks/{artworkId}")
  @Override
  public ApiResponseBody<DisplayArtworkDetailResponse> getDisplayArtworkFullDetail(
      @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkDetailResult result =
        displayArtworkQueryService.getDisplayArtworkFullDetail(artworkId, optionalUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @GetMapping("/api/v1/artworks/{artworkId}/edit")
  @Override
  public ApiResponseBody<DisplayArtworkEditResponse> getDisplayArtworkForEdit(
      @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkEditResult result =
        displayArtworkQueryService.getArtworkForEdit(artworkId, requireUserId(user));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PatchMapping("/api/v1/artworks/{artworkId}")
  @Override
  public ApiResponseBody<DisplayArtworkResponse> updateDisplayArtwork(
      @PathVariable Long artworkId,
      @RequestBody UpdateDisplayArtworkRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkResult result =
        updateDisplayArtworkService.update(
            requireUserId(user), mapper.toCommand(artworkId, request));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping("/api/v1/artworks/{artworkId}")
  @Override
  public ApiResponseBody<DeleteDisplayArtworkResponse> deleteDisplayArtwork(
      @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DeleteDisplayArtworkResult result =
        deleteDisplayArtworkService.delete(requireUserId(user), artworkId);
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PostMapping("/api/v1/artworks/{artworkId}/like")
  @Override
  public ApiResponseBody<DisplayArtworkLikeResponse> likeDisplayArtwork(
      @PathVariable Long artworkId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayArtworkLikeResult result =
        displayArtworkLikeCommandService.like(
            new DisplayArtworkLikeCommand(artworkId, requireUserId(user)));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping("/api/v1/artworks/{artworkId}/like")
  @Override
  public ApiResponseBody<DisplayArtworkLikeResponse> cancelDisplayArtworkLike(
      @PathVariable Long artworkId,
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
