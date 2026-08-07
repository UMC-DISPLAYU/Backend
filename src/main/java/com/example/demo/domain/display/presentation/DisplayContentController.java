package com.example.demo.domain.display.presentation;

import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CATEGORY_ID_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CATEGORY_ID_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CATEGORY_REQUEST_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CONTENT_ID_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CONTENT_ID_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CONTENT_REQUEST_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CATEGORY_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CATEGORY_REQUEST_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CATEGORY_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CATEGORY_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CATEGORY_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CATEGORY_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CATEGORY_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CONTENT_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CONTENT_REQUEST_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CONTENT_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CONTENT_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CONTENT_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CONTENT_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.CREATE_CONTENT_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DELETE_CATEGORY_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DELETE_CATEGORY_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DELETE_CATEGORY_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DELETE_CATEGORY_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DELETE_CATEGORY_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DELETE_CONTENT_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DELETE_CONTENT_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DELETE_CONTENT_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DELETE_CONTENT_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DELETE_CONTENT_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DISPLAY_ID_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.DISPLAY_ID_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.REORDER_CONTENTS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.REORDER_CONTENTS_REQUEST_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.REORDER_CONTENTS_REQUEST_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.REORDER_CONTENTS_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.REORDER_CONTENTS_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.REORDER_CONTENTS_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.REORDER_CONTENTS_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.REORDER_CONTENTS_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.TAG_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.TAG_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CATEGORY_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CATEGORY_REQUEST_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CATEGORY_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CATEGORY_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CATEGORY_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CATEGORY_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CATEGORY_SUMMARY;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CONTENT_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CONTENT_REQUEST_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CONTENT_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CONTENT_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CONTENT_SUCCESS_EXAMPLE;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CONTENT_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.display.presentation.docs.DisplayContentApiDocs.UPDATE_CONTENT_SUMMARY;

import com.example.demo.domain.display.application.command.DisplayContentCommandService;
import com.example.demo.domain.display.application.result.DeleteDisplayContentCategoryResult;
import com.example.demo.domain.display.application.result.DeleteDisplayContentResult;
import com.example.demo.domain.display.application.result.DisplayContentCategoryResult;
import com.example.demo.domain.display.application.result.DisplayContentResult;
import com.example.demo.domain.display.application.result.ReorderDisplayContentsResult;
import com.example.demo.domain.display.presentation.mapper.DisplayContentPresentationMapper;
import com.example.demo.domain.display.presentation.request.CreateDisplayContentCategoryRequest;
import com.example.demo.domain.display.presentation.request.CreateDisplayContentRequest;
import com.example.demo.domain.display.presentation.request.ReorderDisplayContentsRequest;
import com.example.demo.domain.display.presentation.request.UpdateDisplayContentCategoryRequest;
import com.example.demo.domain.display.presentation.request.UpdateDisplayContentRequest;
import com.example.demo.domain.display.presentation.response.DeleteDisplayContentCategoryResponse;
import com.example.demo.domain.display.presentation.response.DeleteDisplayContentResponse;
import com.example.demo.domain.display.presentation.response.DisplayContentCategoryResponse;
import com.example.demo.domain.display.presentation.response.DisplayContentResponse;
import com.example.demo.domain.display.presentation.response.ReorderDisplayContentsResponse;
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
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = TAG_NAME, description = TAG_DESCRIPTION)
public class DisplayContentController {

  private final DisplayContentCommandService displayContentCommandService;
  private final DisplayContentPresentationMapper mapper;

  public DisplayContentController(
      DisplayContentCommandService displayContentCommandService,
      DisplayContentPresentationMapper mapper) {
    this.displayContentCommandService = displayContentCommandService;
    this.mapper = mapper;
  }

  @PostMapping("/api/v1/displays/{displayId}/content-categories")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = CREATE_CATEGORY_SUMMARY, description = CREATE_CATEGORY_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = CREATE_CATEGORY_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = CREATE_CATEGORY_REQUEST_EXAMPLE_NAME,
                      value = CATEGORY_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "201",
      description = CREATE_CATEGORY_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = CREATE_CATEGORY_SUCCESS_EXAMPLE_NAME,
                      value = CREATE_CATEGORY_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayContentCategoryResponse> createCategory(
      @Parameter(description = DISPLAY_ID_DESCRIPTION, example = DISPLAY_ID_EXAMPLE)
          @PathVariable
          @Positive Long displayId,
      @Valid @RequestBody CreateDisplayContentCategoryRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayContentCategoryResult result =
        displayContentCommandService.createCategory(
            mapper.toCommand(request, requireUserId(user), displayId));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PatchMapping("/api/v1/displays/{displayId}/content-categories/{categoryId}")
  @Operation(summary = UPDATE_CATEGORY_SUMMARY, description = UPDATE_CATEGORY_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = UPDATE_CATEGORY_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = UPDATE_CATEGORY_REQUEST_EXAMPLE_NAME,
                      value = CATEGORY_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "200",
      description = UPDATE_CATEGORY_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = UPDATE_CATEGORY_SUCCESS_EXAMPLE_NAME,
                      value = UPDATE_CATEGORY_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayContentCategoryResponse> updateCategory(
      @Parameter(description = DISPLAY_ID_DESCRIPTION, example = DISPLAY_ID_EXAMPLE)
          @PathVariable
          @Positive Long displayId,
      @Parameter(description = CATEGORY_ID_DESCRIPTION, example = CATEGORY_ID_EXAMPLE)
          @PathVariable
          @Positive Long categoryId,
      @Valid @RequestBody UpdateDisplayContentCategoryRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayContentCategoryResult result =
        displayContentCommandService.updateCategory(
            mapper.toCommand(request, requireUserId(user), displayId, categoryId));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping("/api/v1/displays/{displayId}/content-categories/{categoryId}")
  @Operation(summary = DELETE_CATEGORY_SUMMARY, description = DELETE_CATEGORY_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = DELETE_CATEGORY_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = DELETE_CATEGORY_SUCCESS_EXAMPLE_NAME,
                      value = DELETE_CATEGORY_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DeleteDisplayContentCategoryResponse> deleteCategory(
      @Parameter(description = DISPLAY_ID_DESCRIPTION, example = DISPLAY_ID_EXAMPLE)
          @PathVariable
          @Positive Long displayId,
      @Parameter(description = CATEGORY_ID_DESCRIPTION, example = CATEGORY_ID_EXAMPLE)
          @PathVariable
          @Positive Long categoryId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DeleteDisplayContentCategoryResult result =
        displayContentCommandService.deleteCategory(
            mapper.toDeleteCategoryCommand(requireUserId(user), displayId, categoryId));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PostMapping("/api/v1/displays/{displayId}/content-categories/{categoryId}/contents")
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = CREATE_CONTENT_SUMMARY, description = CREATE_CONTENT_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = CREATE_CONTENT_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = CREATE_CONTENT_REQUEST_EXAMPLE_NAME,
                      value = CONTENT_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "201",
      description = CREATE_CONTENT_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = CREATE_CONTENT_SUCCESS_EXAMPLE_NAME,
                      value = CREATE_CONTENT_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayContentResponse> createContent(
      @Parameter(description = DISPLAY_ID_DESCRIPTION, example = DISPLAY_ID_EXAMPLE)
          @PathVariable
          @Positive Long displayId,
      @Parameter(description = CATEGORY_ID_DESCRIPTION, example = CATEGORY_ID_EXAMPLE)
          @PathVariable
          @Positive Long categoryId,
      @Valid @RequestBody CreateDisplayContentRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayContentResult result =
        displayContentCommandService.createContent(
            mapper.toCommand(request, requireUserId(user), displayId, categoryId));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PatchMapping("/api/v1/displays/{displayId}/content-categories/{categoryId}/contents/{contentId}")
  @Operation(summary = UPDATE_CONTENT_SUMMARY, description = UPDATE_CONTENT_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = UPDATE_CONTENT_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = UPDATE_CONTENT_REQUEST_EXAMPLE_NAME,
                      value = CONTENT_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "200",
      description = UPDATE_CONTENT_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = UPDATE_CONTENT_SUCCESS_EXAMPLE_NAME,
                      value = UPDATE_CONTENT_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DisplayContentResponse> updateContent(
      @Parameter(description = DISPLAY_ID_DESCRIPTION, example = DISPLAY_ID_EXAMPLE)
          @PathVariable
          @Positive Long displayId,
      @Parameter(description = CATEGORY_ID_DESCRIPTION, example = CATEGORY_ID_EXAMPLE)
          @PathVariable
          @Positive Long categoryId,
      @Parameter(description = CONTENT_ID_DESCRIPTION, example = CONTENT_ID_EXAMPLE)
          @PathVariable
          @Positive Long contentId,
      @Valid @RequestBody UpdateDisplayContentRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DisplayContentResult result =
        displayContentCommandService.updateContent(
            mapper.toCommand(request, requireUserId(user), displayId, categoryId, contentId));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @DeleteMapping(
      "/api/v1/displays/{displayId}/content-categories/{categoryId}/contents/{contentId}")
  @Operation(summary = DELETE_CONTENT_SUMMARY, description = DELETE_CONTENT_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = DELETE_CONTENT_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = DELETE_CONTENT_SUCCESS_EXAMPLE_NAME,
                      value = DELETE_CONTENT_SUCCESS_EXAMPLE)))
  public ApiResponseBody<DeleteDisplayContentResponse> deleteContent(
      @Parameter(description = DISPLAY_ID_DESCRIPTION, example = DISPLAY_ID_EXAMPLE)
          @PathVariable
          @Positive Long displayId,
      @Parameter(description = CATEGORY_ID_DESCRIPTION, example = CATEGORY_ID_EXAMPLE)
          @PathVariable
          @Positive Long categoryId,
      @Parameter(description = CONTENT_ID_DESCRIPTION, example = CONTENT_ID_EXAMPLE)
          @PathVariable
          @Positive Long contentId,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    DeleteDisplayContentResult result =
        displayContentCommandService.deleteContent(
            mapper.toDeleteContentCommand(requireUserId(user), displayId, categoryId, contentId));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  @PatchMapping("/api/v1/displays/{displayId}/content-categories/{categoryId}/contents/reorder")
  @Operation(summary = REORDER_CONTENTS_SUMMARY, description = REORDER_CONTENTS_DESCRIPTION)
  @SecurityRequirement(name = "Authorization")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = REORDER_CONTENTS_REQUEST_DESCRIPTION,
      required = true,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = REORDER_CONTENTS_REQUEST_EXAMPLE_NAME,
                      value = REORDER_CONTENTS_REQUEST_EXAMPLE)))
  @ApiResponse(
      responseCode = "200",
      description = REORDER_CONTENTS_SUCCESS_DESCRIPTION,
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = REORDER_CONTENTS_SUCCESS_EXAMPLE_NAME,
                      value = REORDER_CONTENTS_SUCCESS_EXAMPLE)))
  public ApiResponseBody<ReorderDisplayContentsResponse> reorderContents(
      @Parameter(description = DISPLAY_ID_DESCRIPTION, example = DISPLAY_ID_EXAMPLE)
          @PathVariable
          @Positive Long displayId,
      @Parameter(description = CATEGORY_ID_DESCRIPTION, example = CATEGORY_ID_EXAMPLE)
          @PathVariable
          @Positive Long categoryId,
      @Valid @RequestBody ReorderDisplayContentsRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    ReorderDisplayContentsResult result =
        displayContentCommandService.reorderContents(
            mapper.toCommand(request, requireUserId(user), displayId, categoryId));
    return ApiResponseBody.success(mapper.toResponse(result), httpRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
