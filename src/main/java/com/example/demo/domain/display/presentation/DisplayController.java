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
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.EXIT_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.EXIT_FORBIDDEN_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.EXIT_FORBIDDEN_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.EXIT_MEMBER_NOT_FOUND_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.EXIT_MEMBER_NOT_FOUND_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.EXIT_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.EXIT_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.EXIT_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.EXIT_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.EXIT_UNAUTHORIZED_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.EXIT_UNAUTHORIZED_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.GRADUATION_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.GRADUATION_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.GRADUATION_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.GRADUATION_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.GRADUATION_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.HIDE_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.HIDE_REQUEST_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.HIDE_REQUEST_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.HIDE_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.HIDE_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.HIDE_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.HIDE_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.HIDE_SUMMARY;
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
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MY_DISPLAY_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MY_DISPLAY_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MY_DISPLAY_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MY_DISPLAY_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MY_DISPLAY_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.PUBLISH_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.PUBLISH_REQUEST_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.PUBLISH_REQUEST_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.PUBLISH_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.PUBLISH_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.PUBLISH_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.PUBLISH_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.PUBLISH_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.RESERVATION_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.RESERVATION_REQUEST_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.RESERVATION_REQUEST_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.RESERVATION_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.RESERVATION_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.RESERVATION_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.RESERVATION_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.RESERVATION_SUMMARY;
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
import com.example.demo.domain.display.application.command.HideDisplayService;
import com.example.demo.domain.display.application.command.PublishDisplayService;
import com.example.demo.domain.display.application.command.UpdateDisplayReservationService;
import com.example.demo.domain.display.application.command.UpdateDisplayService;
import com.example.demo.domain.display.application.query.GetDisplayByInvitationService;
import com.example.demo.domain.display.application.query.GetDisplayDetailService;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.application.result.DisplayInvitationDisableResult;
import com.example.demo.domain.display.application.result.DisplayInvitationResult;
import com.example.demo.domain.display.application.result.DisplayLikeResult;
import com.example.demo.domain.display.application.service.DisplayBookmarkEnrichmentService;
import com.example.demo.domain.display.application.service.ExitDisplayService;
import com.example.demo.domain.display.application.service.GetMyDisplaysService;
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
import com.example.demo.domain.display.presentation.request.HideDisplayRequest;
import com.example.demo.domain.display.presentation.request.PublishDisplayRequest;
import com.example.demo.domain.display.presentation.request.SearchDisplayRequest;
import com.example.demo.domain.display.presentation.request.UpdateDisplayRequest;
import com.example.demo.domain.display.presentation.request.UpdateDisplayReservationRequest;
import com.example.demo.domain.display.presentation.response.ClosingSoonDisplayResponse;
import com.example.demo.domain.display.presentation.response.DisplayDetailResponse;
import com.example.demo.domain.display.presentation.response.DisplayInvitationDisableResponse;
import com.example.demo.domain.display.presentation.response.DisplayInvitationResponse;
import com.example.demo.domain.display.presentation.response.DisplayLikeResponse;
import com.example.demo.domain.display.presentation.response.DisplayMapResponse;
import com.example.demo.domain.display.presentation.response.DuPickResponse;
import com.example.demo.domain.display.presentation.response.GraduationDisplayResponse;
import com.example.demo.domain.display.presentation.response.MyDisplayListResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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
  private final PublishDisplayService publishDisplayService;
  private final HideDisplayService hideDisplayService;
  private final UpdateDisplayReservationService updateDisplayReservationService;
  private final GetDisplayDetailService getDisplayDetailService;
  private final GetDisplayByInvitationService getDisplayByInvitationService;
  private final GetDisplayMapUseCase getDisplayMapUseCase;
  private final GetClosingSoonDisplaysUseCase getClosingSoonDisplaysUseCase;
  private final GetRandomGraduationDisplaysUseCase getRandomGraduationDisplaysUseCase;
  private final GetDuPicksUseCase getDuPicksUseCase;
  private final SearchDisplaysUseCase searchDisplaysUseCase;
  private final DisplayBookmarkEnrichmentService displayBookmarkEnrichmentService;
  private final GetMyDisplaysService getMyDisplaysService;
  private final ExitDisplayService exitDisplayService;
  private final DisplayPresentationMapper mapper;

  public DisplayController(
      CreateDisplayService createDisplayService,
      DisplayLikeCommandService displayLikeCommandService,
      DisplayInvitationCommandService displayInvitationCommandService,
      UpdateDisplayService updateDisplayService,
      PublishDisplayService publishDisplayService,
      HideDisplayService hideDisplayService,
      UpdateDisplayReservationService updateDisplayReservationService,
      GetDisplayDetailService getDisplayDetailService,
      GetDisplayByInvitationService getDisplayByInvitationService,
      GetDisplayMapUseCase getDisplayMapUseCase,
      GetClosingSoonDisplaysUseCase getClosingSoonDisplaysUseCase,
      GetRandomGraduationDisplaysUseCase getRandomGraduationDisplaysUseCase,
      GetDuPicksUseCase getDuPicksUseCase,
      SearchDisplaysUseCase searchDisplaysUseCase,
      DisplayBookmarkEnrichmentService displayBookmarkEnrichmentService,
      GetMyDisplaysService getMyDisplaysService,
      ExitDisplayService exitDisplayService,
      DisplayPresentationMapper mapper) {
    this.createDisplayService = createDisplayService;
    this.displayLikeCommandService = displayLikeCommandService;
    this.displayInvitationCommandService = displayInvitationCommandService;
    this.updateDisplayService = updateDisplayService;
    this.publishDisplayService = publishDisplayService;
    this.hideDisplayService = hideDisplayService;
    this.updateDisplayReservationService = updateDisplayReservationService;
    this.getDisplayDetailService = getDisplayDetailService;
    this.getDisplayByInvitationService = getDisplayByInvitationService;
    this.getDisplayMapUseCase = getDisplayMapUseCase;
    this.getClosingSoonDisplaysUseCase = getClosingSoonDisplaysUseCase;
    this.getRandomGraduationDisplaysUseCase = getRandomGraduationDisplaysUseCase;
    this.getDuPicksUseCase = getDuPicksUseCase;
    this.searchDisplaysUseCase = searchDisplaysUseCase;
    this.displayBookmarkEnrichmentService = displayBookmarkEnrichmentService;
    this.getMyDisplaysService = getMyDisplaysService;
    this.exitDisplayService = exitDisplayService;
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
    DisplayDetailResult result =
        displayBookmarkEnrichmentService.enrich(
            getDisplayDetailService.getDisplayDetail(displayId, user.userId()), user.userId());
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @DeleteMapping("/api/v1/display/{displayId}/exit")
  @Operation(summary = EXIT_SUMMARY, description = EXIT_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = EXIT_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = EXIT_SUCCESS_EXAMPLE_NAME, value = EXIT_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "인증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = EXIT_UNAUTHORIZED_EXAMPLE_NAME,
                      value = EXIT_UNAUTHORIZED_EXAMPLE)))
  @ApiResponse(
      responseCode = "403",
      description = "전시 팀장 나가기 제한",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = EXIT_FORBIDDEN_EXAMPLE_NAME,
                      value = EXIT_FORBIDDEN_EXAMPLE)))
  @ApiResponse(
      responseCode = "404",
      description = "전시 멤버를 찾을 수 없음",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = EXIT_MEMBER_NOT_FOUND_EXAMPLE_NAME,
                      value = EXIT_MEMBER_NOT_FOUND_EXAMPLE)))
  public ApiResponseBody<Void> exitDisplay(
      @Parameter(description = DETAIL_DISPLAY_ID_DESCRIPTION, example = DETAIL_DISPLAY_ID_EXAMPLE)
          @PathVariable
          Long displayId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    exitDisplayService.exit(displayId, requireUserId(user));
    return ApiResponseBody.success(null, request);
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
    result = displayBookmarkEnrichmentService.enrich(result, user.userId());
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PatchMapping("/api/v1/display/publish")
  @Operation(summary = PUBLISH_SUMMARY, description = PUBLISH_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = PUBLISH_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = PUBLISH_REQUEST_EXAMPLE_NAME,
                      value = PUBLISH_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "200",
      description = PUBLISH_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = PUBLISH_SUCCESS_EXAMPLE_NAME,
                      value = PUBLISH_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayDetailResponse> publishDisplay(
      @Valid @RequestBody PublishDisplayRequest publishDisplayRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    DisplayDetailResult result =
        publishDisplayService.publishDisplay(publishDisplayRequest.toCommand(requireUserId(user)));
    result = displayBookmarkEnrichmentService.enrich(result, user.userId());
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PatchMapping("/api/v1/display/status")
  @Operation(summary = HIDE_SUMMARY, description = HIDE_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = HIDE_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = HIDE_REQUEST_EXAMPLE_NAME, value = HIDE_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "200",
      description = HIDE_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = HIDE_SUCCESS_EXAMPLE_NAME, value = HIDE_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayDetailResponse> hideDisplay(
      @Valid @RequestBody HideDisplayRequest hideDisplayRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    DisplayDetailResult result =
        hideDisplayService.hideDisplay(hideDisplayRequest.toCommand(requireUserId(user)));
    result = displayBookmarkEnrichmentService.enrich(result, user.userId());
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @PatchMapping("/api/v1/display/{displayId}/reservation")
  @Operation(summary = RESERVATION_SUMMARY, description = RESERVATION_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = RESERVATION_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = RESERVATION_REQUEST_EXAMPLE_NAME,
                      value = RESERVATION_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "200",
      description = RESERVATION_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = RESERVATION_SUCCESS_EXAMPLE_NAME,
                      value = RESERVATION_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayDetailResponse> updateDisplayReservation(
      @Parameter(description = DETAIL_DISPLAY_ID_DESCRIPTION, example = DETAIL_DISPLAY_ID_EXAMPLE)
          @PathVariable
          Long displayId,
      @Valid @RequestBody UpdateDisplayReservationRequest updateDisplayReservationRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    DisplayDetailResult result =
        updateDisplayReservationService.updateReservation(
            updateDisplayReservationRequest.toCommand(requireUserId(user), displayId));
    result = displayBookmarkEnrichmentService.enrich(result, user.userId());
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
  @SecurityRequirement(name = "Authorization")
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
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    Long requesterUserId = requireUserId(user);
    DisplayDetailResult result =
        displayBookmarkEnrichmentService.enrich(
            getDisplayByInvitationService.getDisplay(token, requesterUserId), requesterUserId);
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
      @Valid @ModelAttribute DisplayMapRequest displayMapRequest,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            displayBookmarkEnrichmentService.enrich(
                getDisplayMapUseCase.getDisplayMap(displayMapRequest.toQuery()),
                userIdOrNull(user))),
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
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            displayBookmarkEnrichmentService.enrich(
                searchDisplaysUseCase.searchDisplays(searchDisplayRequest.toQuery()),
                userIdOrNull(user))),
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
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            displayBookmarkEnrichmentService.enrich(
                getClosingSoonDisplaysUseCase.getClosingSoonDisplays(
                    closingSoonDisplayRequest.toQuery()),
                userIdOrNull(user))),
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
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(
            displayBookmarkEnrichmentService.enrich(
                getRandomGraduationDisplaysUseCase.getRandomGraduationDisplays(
                    graduationDisplayRequest.requestedSize()),
                userIdOrNull(user))),
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

  @GetMapping("/api/v1/display/me")
  @Operation(summary = MY_DISPLAY_SUMMARY, description = MY_DISPLAY_DESCRIPTION)
  @ApiResponse(
      responseCode = "200",
      description = MY_DISPLAY_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = MY_DISPLAY_SUCCESS_EXAMPLE_NAME,
                      value = MY_DISPLAY_SUCCESS_EXAMPLE)))
  @SecurityRequirement(name = "Authorization")
  public ApiResponseBody<MyDisplayListResponse> getMyDisplays(
      @AuthenticationPrincipal AuthUser user, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(getMyDisplaysService.getMyDisplays(requireUserId(user))), request);
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
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest request) {
    DisplayDetailResult result =
        displayBookmarkEnrichmentService.enrich(
            getDisplayDetailService.getDisplayDetail(displayId, userIdOrNull(user)),
            userIdOrNull(user));
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  private Long userIdOrNull(AuthUser user) {
    return user == null ? null : user.userId();
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
