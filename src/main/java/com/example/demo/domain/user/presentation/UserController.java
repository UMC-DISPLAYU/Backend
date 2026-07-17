package com.example.demo.domain.user.presentation;

import com.example.demo.domain.artist.application.result.ArtistProfileResult;
import com.example.demo.domain.artist.application.service.GetArtistProfileService;
import com.example.demo.domain.artist.presentation.mapper.ArtistProfileMapper;
import com.example.demo.domain.artist.presentation.response.MyArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UserArtistProfileResponse;
import com.example.demo.domain.user.application.result.MyUserResult;
import com.example.demo.domain.user.application.service.GetMyUserService;
import com.example.demo.domain.user.application.service.ResendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.SendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.UserService;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import com.example.demo.domain.user.presentation.docs.UserControllerDocs;
import com.example.demo.domain.user.presentation.mapper.UserPresentationMapper;
import com.example.demo.domain.user.presentation.response.MyUserResponse;
import com.example.demo.domain.user.presentation.response.NicknameCheckResponse;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {

  private final UserService userService;
  private final SendSchoolEmailVerificationService sendSchoolEmailVerificationService;
  private final ResendSchoolEmailVerificationService resendSchoolEmailVerificationService;
  private final GetMyUserService getMyUserService;
  private final GetArtistProfileService getArtistProfileService;
  private final UserPresentationMapper userPresentationMapper;
  private final ArtistProfileMapper artistProfileMapper;

  @Override
  @GetMapping("/me")
  public ApiResponseBody<MyUserResponse> getMe(
      @AuthenticationPrincipal AuthUser user, HttpServletRequest httpRequest) {
    MyUserResult result = getMyUserService.execute(user.userId());
    return ApiResponseBody.success(userPresentationMapper.toResponse(result), httpRequest);
  }

  @Override
  @GetMapping("/me/artist-profile")
  public ApiResponseBody<MyArtistProfileResponse> getMyArtistProfile(
      @AuthenticationPrincipal AuthUser user, HttpServletRequest httpRequest) {
    ArtistProfileResult result = getArtistProfileService.getMine(user.userId());
    return ApiResponseBody.success(artistProfileMapper.toMyResponse(result), httpRequest);
  }

  @Override
  @GetMapping("/{userId}/artist-profile")
  public ApiResponseBody<UserArtistProfileResponse> getUserArtistProfile(
      @PathVariable Long userId, HttpServletRequest httpRequest) {
    ArtistProfileResult result = getArtistProfileService.getByUserId(userId);
    return ApiResponseBody.success(artistProfileMapper.toUserResponse(result), httpRequest);
  }

  @Override
  @GetMapping("/nickname/check")
  public ApiResponseBody<NicknameCheckResponse> checkNickname(
      @RequestParam(value = "nickname", required = false) String nickname,
      HttpServletRequest httpRequest) {

    if (nickname == null || nickname.isBlank()) {
      throw new UserException(UserErrorCode.MISSING_NICKNAME);
    }

    boolean isAvailable = userService.isNicknameAvailable(nickname);

    return ApiResponseBody.success(new NicknameCheckResponse(nickname, isAvailable), httpRequest);
  }
}
