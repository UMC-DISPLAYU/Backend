package com.example.demo.domain.display.presentation;

import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CLOSING_SOON_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CLOSING_SOON_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CLOSING_SOON_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CLOSING_SOON_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CLOSING_SOON_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CREATE_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CREATE_REQUEST_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CREATE_REQUEST_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CREATE_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CREATE_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CREATE_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CREATE_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CREATE_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DETAIL_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DETAIL_DISPLAY_ID_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DETAIL_DISPLAY_ID_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DETAIL_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DETAIL_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DETAIL_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DETAIL_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DU_PICKS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DU_PICKS_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DU_PICKS_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DU_PICKS_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.DU_PICKS_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.GRADUATION_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.GRADUATION_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.GRADUATION_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.GRADUATION_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.GRADUATION_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DETAIL_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DETAIL_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DETAIL_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DETAIL_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DETAIL_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DISABLE_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DISABLE_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DISABLE_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DISABLE_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DISABLE_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DISPLAY_ID_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_DISPLAY_ID_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_ISSUE_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_ISSUE_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_ISSUE_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_ISSUE_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_ISSUE_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_TOKEN_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.INVITATION_TOKEN_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_CANCEL_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_CANCEL_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_CANCEL_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_CANCEL_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_REQUEST_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_REQUEST_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.LIKE_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MAP_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MAP_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MAP_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MAP_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MAP_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.SEARCH_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.SEARCH_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.SEARCH_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.SEARCH_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.SEARCH_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.TAG_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.TAG_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.UPDATE_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.UPDATE_REQUEST_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.UPDATE_REQUEST_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.UPDATE_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.UPDATE_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.UPDATE_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.UPDATE_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.UPDATE_SUMMARY;

import com.example.demo.domain.display.application.command.CreateDisplayService;
import com.example.demo.domain.display.application.command.DisplayInvitationCommandService;
import com.example.demo.domain.display.application.command.DisplayLikeCommandService;
import com.example.demo.domain.display.application.command.UpdateDisplayService;
import com.example.demo.domain.display.application.query.GetDisplayByInvitationService;
import com.example.demo.domain.display.application.query.GetDisplayDetailService;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.application.result.DisplayInvitationDisableResult;
import com.example.demo.domain.display.application.result.DisplayInvitationResult;
import com.example.demo.domain.display.application.result.DisplayLikeResult;
import com.example.demo.domain.display.application.usecase.GetClosingSoonDisplaysUseCase;
import com.example.demo.domain.display.application.usecase.GetDisplayMapUseCase;
import com.example.demo.domain.display.application.usecase.GetDuPicksUseCase;
import com.example.demo.domain.display.application.usecase.GetRandomGraduationDisplaysUseCase;
import com.example.demo.domain.display.application.usecase.SearchDisplaysUseCase;
import com.example.demo.domain.display.presentation.mapper.DisplayPresentationMapper;
import com.example.demo.domain.display.presentation.request.ClosingSoonDisplayRequest;
import com.example.demo.domain.display.presentation.request.CreateDisplayRequest;
import com.example.demo.domain.display.presentation.request.DisplayLikeRequest;
import com.example.demo.domain.display.presentation.request.DisplayMapRequest;
import com.example.demo.domain.display.presentation.request.DuPickRequest;
import com.example.demo.domain.display.presentation.request.GraduationDisplayRequest;
import com.example.demo.domain.display.presentation.request.SearchDisplayRequest;
import com.example.demo.domain.display.presentation.request.UpdateDisplayRequest;
import com.example.demo.domain.display.presentation.response.ClosingSoonDisplayResponse;
import com.example.demo.domain.display.presentation.response.DisplayDetailResponse;
import com.example.demo.domain.display.presentation.response.DisplayInvitationDisableResponse;
import com.example.demo.domain.display.presentation.response.DisplayInvitationResponse;
import com.example.demo.domain.display.presentation.response.DisplayLikeResponse;
import com.example.demo.domain.display.presentation.response.DisplayMapResponse;
import com.example.demo.domain.display.presentation.response.DuPickResponse;
import com.example.demo.domain.display.presentation.response.GraduationDisplayResponse;
import com.example.demo.domain.display.presentation.response.SearchDisplayResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = TAG_NAME, description = TAG_DESCRIPTION)
public class DisplayController {

  private final CreateDisplayService createDisplayService;
  private final DisplayLikeCommandService displayLikeCommandService;
  private final DisplayInvitationCommandService displayInvitationCommandService;
  private final UpdateDisplayService updateDisplayService;
  private final GetDisplayDetailService getDisplayDetailService;
  private final GetDisplayByInvitationService getDisplayByInvitationService;
  private final GetDisplayMapUseCase getDisplayMapUseCase;
  private final GetClosingSoonDisplaysUseCase getClosingSoonDisplaysUseCase;
  private final GetRandomGraduationDisplaysUseCase getRandomGraduationDisplaysUseCase;
  private final GetDuPicksUseCase getDuPicksUseCase;
  private final SearchDisplaysUseCase searchDisplaysUseCase;
  private final DisplayPresentationMapper mapper;

  public DisplayController(
      CreateDisplayService createDisplayService,
      DisplayLikeCommandService displayLikeCommandService,
      DisplayInvitationCommandService displayInvitationCommandService,
      UpdateDisplayService updateDisplayService,
      GetDisplayDetailService getDisplayDetailService,
      GetDisplayByInvitationService getDisplayByInvitationService,
      GetDisplayMapUseCase getDisplayMapUseCase,
      GetClosingSoonDisplaysUseCase getClosingSoonDisplaysUseCase,
      GetRandomGraduationDisplaysUseCase getRandomGraduationDisplaysUseCase,
      GetDuPicksUseCase getDuPicksUseCase,
      SearchDisplaysUseCase searchDisplaysUseCase,
      DisplayPresentationMapper mapper) {
    this.createDisplayService = createDisplayService;
    this.displayLikeCommandService = displayLikeCommandService;
    this.displayInvitationCommandService = displayInvitationCommandService;
    this.updateDisplayService = updateDisplayService;
    this.getDisplayDetailService = getDisplayDetailService;
    this.getDisplayByInvitationService = getDisplayByInvitationService;
    this.getDisplayMapUseCase = getDisplayMapUseCase;
    this.getClosingSoonDisplaysUseCase = getClosingSoonDisplaysUseCase;
    this.getRandomGraduationDisplaysUseCase = getRandomGraduationDisplaysUseCase;
    this.getDuPicksUseCase = getDuPicksUseCase;
    this.searchDisplaysUseCase = searchDisplaysUseCase;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/display")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = CREATE_SUMMARY, description = CREATE_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = CREATE_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = CREATE_REQUEST_EXAMPLE_NAME,
                      value = CREATE_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "201",
      description = CREATE_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = CREATE_SUCCESS_EXAMPLE_NAME,
                      value = CREATE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayDetailResponse> createDisplay(
      @Valid @RequestBody CreateDisplayRequest createDisplayRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    Long displayId =
        createDisplayService
            .createDisplay(mapper.toCommand(createDisplayRequest, requireUserId(user)))
            .displayId();
    DisplayDetailResult result = getDisplayDetailService.getDisplayDetail(displayId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PatchMapping("/api/v1/display")
  @Operation(summary = UPDATE_SUMMARY, description = UPDATE_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = UPDATE_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = UPDATE_REQUEST_EXAMPLE_NAME,
                      value = UPDATE_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "200",
      description = UPDATE_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = UPDATE_SUCCESS_EXAMPLE_NAME,
                      value = UPDATE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayDetailResponse> updateDisplay(
      @Valid @RequestBody UpdateDisplayRequest updateDisplayRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    DisplayDetailResult result =
        updateDisplayService.updateDisplay(
            mapper.toCommand(updateDisplayRequest, requireUserId(user)));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PostMapping("/api/v1/display/like")
  @Operation(summary = LIKE_SUMMARY, description = LIKE_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = LIKE_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = LIKE_REQUEST_EXAMPLE_NAME, value = LIKE_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "200",
      description = LIKE_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = LIKE_SUCCESS_EXAMPLE_NAME, value = LIKE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayLikeResponse> likeDisplay(
      @Valid @RequestBody DisplayLikeRequest displayLikeRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    DisplayLikeResult result =
        displayLikeCommandService.like(displayLikeRequest.toCommand(requireUserId(user)));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PatchMapping("/api/v1/display/like")
  @Operation(summary = LIKE_CANCEL_SUMMARY, description = LIKE_CANCEL_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = LIKE_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = LIKE_REQUEST_EXAMPLE_NAME, value = LIKE_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "200",
      description = LIKE_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = LIKE_CANCEL_SUCCESS_EXAMPLE_NAME,
                      value = LIKE_CANCEL_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayLikeResponse> cancelLikeDisplay(
      @Valid @RequestBody DisplayLikeRequest displayLikeRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    DisplayLikeResult result =
        displayLikeCommandService.cancel(displayLikeRequest.toCommand(requireUserId(user)));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PostMapping("/api/v1/display/{displayId}/invitation")
  @Operation(summary = INVITATION_ISSUE_SUMMARY, description = INVITATION_ISSUE_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = INVITATION_ISSUE_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = INVITATION_ISSUE_SUCCESS_EXAMPLE_NAME,
                      value = INVITATION_ISSUE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayInvitationResponse> issueDisplayInvitation(
      @Parameter(
              description = INVITATION_DISPLAY_ID_DESCRIPTION,
              example = INVITATION_DISPLAY_ID_EXAMPLE)
          @PathVariable
          Long displayId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    DisplayInvitationResult result =
        displayInvitationCommandService.issueInvitation(requireUserId(user), displayId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PatchMapping("/api/v1/display/{displayId}/invitation/disable")
  @Operation(summary = INVITATION_DISABLE_SUMMARY, description = INVITATION_DISABLE_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = INVITATION_DISABLE_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = INVITATION_DISABLE_SUCCESS_EXAMPLE_NAME,
                      value = INVITATION_DISABLE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayInvitationDisableResponse> disableDisplayInvitation(
      @Parameter(
              description = INVITATION_DISPLAY_ID_DESCRIPTION,
              example = INVITATION_DISPLAY_ID_EXAMPLE)
          @PathVariable
          Long displayId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    DisplayInvitationDisableResult result =
        displayInvitationCommandService.disableInvitation(requireUserId(user), displayId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/display/invitation/{token}")
  @Operation(summary = INVITATION_DETAIL_SUMMARY, description = INVITATION_DETAIL_DESCRIPTION)
  @ApiResponse(
      responseCode = "200",
      description = INVITATION_DETAIL_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = INVITATION_DETAIL_SUCCESS_EXAMPLE_NAME,
                      value = INVITATION_DETAIL_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayDetailResponse> getDisplayByInvitation(
      @Parameter(description = INVITATION_TOKEN_DESCRIPTION, example = INVITATION_TOKEN_EXAMPLE)
          @PathVariable
          String token,
      HttpServletRequest request) {
    DisplayDetailResult result = getDisplayByInvitationService.getDisplay(token);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/display/map")
  @Operation(summary = MAP_SUMMARY, description = MAP_DESCRIPTION)
  @ApiResponse(
      responseCode = "200",
      description = MAP_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = MAP_SUCCESS_EXAMPLE_NAME, value = MAP_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayMapResponse> getDisplayMap(
      @Valid @ModelAttribute DisplayMapRequest displayMapRequest, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(getDisplayMapUseCase.getDisplayMap(displayMapRequest.toQuery())),
        request);
  }

  @GetMapping("/api/v1/display/search")
  @Operation(summary = SEARCH_SUMMARY, description = SEARCH_DESCRIPTION)
  @ApiResponse(
      responseCode = "200",
      description = SEARCH_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = SEARCH_SUCCESS_EXAMPLE_NAME,
                      value = SEARCH_SUCCESS_EXAMPLE)))
  public ApiResponseBody<SearchDisplayResponse> searchDisplays(
      @Valid @ModelAttribute SearchDisplayRequest searchDisplayRequest,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(searchDisplaysUseCase.searchDisplays(searchDisplayRequest.toQuery())),
        request);
  }

  @GetMapping("/api/v1/display/closing-soon")
  @Operation(summary = CLOSING_SOON_SUMMARY, description = CLOSING_SOON_DESCRIPTION)
  @ApiResponse(
      responseCode = "200",
      description = CLOSING_SOON_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = CLOSING_SOON_SUCCESS_EXAMPLE_NAME,
                      value = CLOSING_SOON_SUCCESS_EXAMPLE)))
  public ApiResponseBody<ClosingSoonDisplayResponse> getClosingSoonDisplays(
      @Valid @ModelAttribute ClosingSoonDisplayRequest closingSoonDisplayRequest,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            getClosingSoonDisplaysUseCase.getClosingSoonDisplays(
                closingSoonDisplayRequest.toQuery())),
        request);
  }

  @GetMapping("/api/v1/display/graduation")
  @Operation(summary = GRADUATION_SUMMARY, description = GRADUATION_DESCRIPTION)
  @ApiResponse(
      responseCode = "200",
      description = GRADUATION_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = GRADUATION_SUCCESS_EXAMPLE_NAME,
                      value = GRADUATION_SUCCESS_EXAMPLE)))
  public ApiResponseBody<GraduationDisplayResponse> getRandomGraduationDisplays(
      @Valid @ModelAttribute GraduationDisplayRequest graduationDisplayRequest,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            getRandomGraduationDisplaysUseCase.getRandomGraduationDisplays(
                graduationDisplayRequest.requestedSize())),
        request);
  }

  @GetMapping("/api/v1/display/du-picks")
  @Operation(summary = DU_PICKS_SUMMARY, description = DU_PICKS_DESCRIPTION)
  @ApiResponse(
      responseCode = "200",
      description = DU_PICKS_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = DU_PICKS_SUCCESS_EXAMPLE_NAME,
                      value = DU_PICKS_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DuPickResponse> getDuPicks(
      @Valid @ModelAttribute DuPickRequest duPickRequest, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(getDuPicksUseCase.getDuPicks(duPickRequest.toQuery())), request);
  }

  @GetMapping("/api/v1/display/{displayId}")
  @Operation(summary = DETAIL_SUMMARY, description = DETAIL_DESCRIPTION)
  @ApiResponse(
      responseCode = "200",
      description = DETAIL_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = DETAIL_SUCCESS_EXAMPLE_NAME,
                      value = DETAIL_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayDetailResponse> getDisplayDetail(
      @Parameter(description = DETAIL_DISPLAY_ID_DESCRIPTION, example = DETAIL_DISPLAY_ID_EXAMPLE)
          @PathVariable
          Long displayId,
      HttpServletRequest request) {
    DisplayDetailResult result = getDisplayDetailService.getDisplayDetail(displayId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
