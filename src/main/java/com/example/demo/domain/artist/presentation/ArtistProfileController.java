package com.example.demo.domain.artist.presentation;

import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.CREATE_DESCRIPTION;
import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.CREATE_REQUEST_DESCRIPTION;
import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.CREATE_REQUEST_EXAMPLE;
import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.CREATE_REQUEST_EXAMPLE_NAME;
import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.CREATE_SUCCESS_DESCRIPTION;
import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.CREATE_SUCCESS_EXAMPLE;
import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.CREATE_SUCCESS_EXAMPLE_NAME;
import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.CREATE_SUMMARY;
import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.TAG_DESCRIPTION;
import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.TAG_NAME;

import com.example.demo.domain.artist.application.service.CreateArtistProfileService;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.presentation.mapper.ArtistProfileMapper;
import com.example.demo.domain.artist.presentation.request.CreateArtistProfileRequest;
import com.example.demo.domain.artist.presentation.response.CreateArtistProfileResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/artists/me/artist-profile")
@Tag(name = TAG_NAME, description = TAG_DESCRIPTION)
public class ArtistProfileController {

  private final CreateArtistProfileService createArtistProfileService;
  private final ArtistProfileMapper artistProfileMapper;

  @PostMapping
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
  @SecurityRequirement(name = "Authorization")
  public ApiResponseBody<CreateArtistProfileResponse> create(
      @Valid @RequestBody CreateArtistProfileRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    ArtistProfile artistProfile =
        createArtistProfileService.execute(user.userId(), request.toCommand());
    return ApiResponseBody.success(
        artistProfileMapper.toResponse(artistProfile, request.activityFields()), httpRequest);
  }
}
