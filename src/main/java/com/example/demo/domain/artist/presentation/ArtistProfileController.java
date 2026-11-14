package com.example.demo.domain.artist.presentation;

import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.TAG_DESCRIPTION;
import static com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs.TAG_NAME;

import com.example.demo.domain.artist.application.result.ArtistProfileResult;
import com.example.demo.domain.artist.application.result.UpdateArtistProfileResult;
import com.example.demo.domain.artist.application.service.CreateArtistProfileService;
import com.example.demo.domain.artist.application.service.GetArtistProfileService;
import com.example.demo.domain.artist.application.service.UpdateArtistProfileService;
import com.example.demo.domain.artist.domain.aggregate.ArtistProfile;
import com.example.demo.domain.artist.presentation.docs.ArtistProfileControllerDocs;
import com.example.demo.domain.artist.presentation.mapper.ArtistProfileMapper;
import com.example.demo.domain.artist.presentation.request.CreateArtistProfileRequest;
import com.example.demo.domain.artist.presentation.request.UpdateArtistProfileRequest;
import com.example.demo.domain.artist.presentation.response.CreateArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.MyArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UpdateArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UserArtistProfileResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/artists")
@Tag(name = TAG_NAME, description = TAG_DESCRIPTION)
public class ArtistProfileController implements ArtistProfileControllerDocs {

  private final CreateArtistProfileService createArtistProfileService;
  private final GetArtistProfileService getArtistProfileService;
  private final UpdateArtistProfileService updateArtistProfileService;
  private final ArtistProfileMapper artistProfileMapper;

  @Override
  @PostMapping("/me/artist-profile")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponseBody<CreateArtistProfileResponse> create(
      @Valid @RequestBody CreateArtistProfileRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {
    ArtistProfile artistProfile =
        createArtistProfileService.execute(requireUserId(user), request.toCommand());
    return ApiResponseBody.success(
        artistProfileMapper.toResponse(artistProfile, request.activityFields()), httpRequest);
  }

  @Override
  @GetMapping("/me/artist-profile")
  public ApiResponseBody<MyArtistProfileResponse> getMyArtistProfile(
      @AuthenticationPrincipal AuthUser user, HttpServletRequest httpRequest) {
    ArtistProfileResult result = getArtistProfileService.getMine(requireUserId(user));
    return ApiResponseBody.success(artistProfileMapper.toMyResponse(result), httpRequest);
  }

  @Override
  @PatchMapping("/me/artist-profile")
  public ApiResponseBody<UpdateArtistProfileResponse> updateMyArtistProfile(
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody UpdateArtistProfileRequest request,
      HttpServletRequest httpRequest) {
    UpdateArtistProfileResult result =
        updateArtistProfileService.execute(
            artistProfileMapper.toCommand(requireUserId(user), request));
    return ApiResponseBody.success(artistProfileMapper.toResponse(result), httpRequest);
  }

  @Override
  @GetMapping("/{userId}/artist-profile")
  public ApiResponseBody<UserArtistProfileResponse> getUserArtistProfile(
      @PathVariable Long userId, HttpServletRequest httpRequest) {
    ArtistProfileResult result = getArtistProfileService.getByUserId(userId);
    return ApiResponseBody.success(artistProfileMapper.toUserResponse(result), httpRequest);
  }

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
