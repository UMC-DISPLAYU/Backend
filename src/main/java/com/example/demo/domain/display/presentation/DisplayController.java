package com.example.demo.domain.display.presentation;

import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.CLOSING_SOON_DESCRIPTION;
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
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MAP_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.MAP_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.TAG_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayApiDocs.TAG_NAME;

import com.example.demo.domain.display.application.command.CreateDisplayService;
import com.example.demo.domain.display.application.query.GetDisplayDetailService;
import com.example.demo.domain.display.application.result.DisplayDetailResult;
import com.example.demo.domain.display.application.usecase.GetClosingSoonDisplaysUseCase;
import com.example.demo.domain.display.application.usecase.GetDisplayMapUseCase;
import com.example.demo.domain.display.presentation.mapper.DisplayPresentationMapper;
import com.example.demo.domain.display.presentation.request.CreateDisplayRequest;
import com.example.demo.domain.display.presentation.request.DisplayMapRequest;
import com.example.demo.domain.display.presentation.response.ClosingSoonDisplayResponse;
import com.example.demo.domain.display.presentation.response.DisplayDetailResponse;
import com.example.demo.domain.display.presentation.response.DisplayMapResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = TAG_NAME, description = TAG_DESCRIPTION)
public class DisplayController {

  private static final Long TEMP_OWNER_USER_ID = 1L;

  private final CreateDisplayService createDisplayService;
  private final GetDisplayDetailService getDisplayDetailService;
  private final GetDisplayMapUseCase getDisplayMapUseCase;
  private final GetClosingSoonDisplaysUseCase getClosingSoonDisplaysUseCase;
  private final DisplayPresentationMapper mapper;

  public DisplayController(
      CreateDisplayService createDisplayService,
      GetDisplayDetailService getDisplayDetailService,
      GetDisplayMapUseCase getDisplayMapUseCase,
      GetClosingSoonDisplaysUseCase getClosingSoonDisplaysUseCase,
      DisplayPresentationMapper mapper) {
    this.createDisplayService = createDisplayService;
    this.getDisplayDetailService = getDisplayDetailService;
    this.getDisplayMapUseCase = getDisplayMapUseCase;
    this.getClosingSoonDisplaysUseCase = getClosingSoonDisplaysUseCase;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/display")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = CREATE_SUMMARY, description = CREATE_DESCRIPTION)
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
      @Valid @RequestBody CreateDisplayRequest createDisplayRequest, HttpServletRequest request) {
    Long displayId =
        createDisplayService
            .createDisplay(createDisplayRequest.toCommand(TEMP_OWNER_USER_ID))
            .displayId();
    DisplayDetailResult result = getDisplayDetailService.getDisplayDetail(displayId);
    return ApiResponseBody.success(mapper.toResponse(result), request);
  }

  @GetMapping("/api/v1/display/map")
  @Operation(summary = MAP_SUMMARY, description = MAP_DESCRIPTION)
  public ApiResponseBody<DisplayMapResponse> getDisplayMap(
      @Valid @ModelAttribute DisplayMapRequest displayMapRequest, HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(getDisplayMapUseCase.getDisplayMap(displayMapRequest.toQuery())),
        request);
  }

  @GetMapping("/api/v1/display/closing-soon")
  @Operation(summary = CLOSING_SOON_SUMMARY, description = CLOSING_SOON_DESCRIPTION)
  public ApiResponseBody<ClosingSoonDisplayResponse> getClosingSoonDisplays(
      HttpServletRequest request) {
    return ApiResponseBody.success(
        mapper.toResponse(getClosingSoonDisplaysUseCase.getClosingSoonDisplays()), request);
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
}
