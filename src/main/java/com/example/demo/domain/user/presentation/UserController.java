package com.example.demo.domain.user.presentation;

import com.example.demo.domain.artist.application.result.ArtistProfileResult;
import com.example.demo.domain.artist.application.result.UpdateArtistProfileResult;
import com.example.demo.domain.artist.application.service.GetArtistProfileService;
import com.example.demo.domain.artist.application.service.UpdateArtistProfileService;
import com.example.demo.domain.artist.presentation.mapper.ArtistProfileMapper;
import com.example.demo.domain.artist.presentation.request.UpdateArtistProfileRequest;
import com.example.demo.domain.artist.presentation.response.MyArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UpdateArtistProfileResponse;
import com.example.demo.domain.artist.presentation.response.UserArtistProfileResponse;
import com.example.demo.domain.user.application.result.ChangeNicknameResult;
import com.example.demo.domain.user.application.result.MyUserResult;
import com.example.demo.domain.user.application.result.UpdateMyProfileResult;
import com.example.demo.domain.user.application.service.ChangeNicknameService;
import com.example.demo.domain.user.application.service.GetMyUserService;
import com.example.demo.domain.user.application.service.ResendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.SendSchoolEmailVerificationService;
import com.example.demo.domain.user.application.service.UpdateMyProfileService;
import com.example.demo.domain.user.application.service.UserService;
import com.example.demo.domain.user.application.service.WithdrawUserService;
import com.example.demo.domain.user.exception.UserErrorCode;
import com.example.demo.domain.user.exception.UserException;
import com.example.demo.domain.user.presentation.docs.UserControllerDocs;
import com.example.demo.domain.user.presentation.mapper.UserPresentationMapper;
import com.example.demo.domain.user.presentation.request.ChangeNicknameRequest;
import com.example.demo.domain.user.presentation.request.UpdateMyProfileRequest;
import com.example.demo.domain.user.presentation.response.ChangeNicknameResponse;
import com.example.demo.domain.user.presentation.response.MyUserResponse;
import com.example.demo.domain.user.presentation.response.NicknameCheckResponse;
import com.example.demo.domain.user.presentation.response.UpdateMyProfileResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalErrorCode;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
  private final WithdrawUserService withdrawUserService;
  private final ChangeNicknameService changeNicknameService;
  private final UpdateMyProfileService updateMyProfileService;
  private final GetArtistProfileService getArtistProfileService;
  private final UpdateArtistProfileService updateArtistProfileService;
  private final UserPresentationMapper userPresentationMapper;
  private final ArtistProfileMapper artistProfileMapper;

  @Override
  @GetMapping("/me")
  public ApiResponseBody<MyUserResponse> getMe(
      @AuthenticationPrincipal AuthUser user, HttpServletRequest httpRequest) {
    MyUserResult result = getMyUserService.execute(requireUserId(user));
    return ApiResponseBody.success(userPresentationMapper.toResponse(result), httpRequest);
  }

  @Override
  @PatchMapping("/me")
  public ApiResponseBody<UpdateMyProfileResponse> updateMe(
      @AuthenticationPrincipal AuthUser user,
      @RequestBody UpdateMyProfileRequest request,
      HttpServletRequest httpRequest) {
    UpdateMyProfileResult result =
        updateMyProfileService.execute(userPresentationMapper.toCommand(user.userId(), request));
    return ApiResponseBody.success(userPresentationMapper.toResponse(result), httpRequest);
  }

  @Override
  @DeleteMapping("/me")
  public ApiResponseBody<Void> withdraw(
      @AuthenticationPrincipal AuthUser user, HttpServletRequest httpRequest) {
    withdrawUserService.execute(userPresentationMapper.toWithdrawCommand(user.userId()));
    return ApiResponseBody.success(null, httpRequest);
  }

  @Override
  @PatchMapping("/me/nickname")
  public ApiResponseBody<ChangeNicknameResponse> changeNickname(
      @AuthenticationPrincipal AuthUser user,
      @RequestBody ChangeNicknameRequest request,
      HttpServletRequest httpRequest) {
    ChangeNicknameResult result =
        changeNicknameService.execute(userPresentationMapper.toCommand(user.userId(), request));
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
  @PatchMapping("/me/artist-profile")
  public ApiResponseBody<UpdateArtistProfileResponse> updateMyArtistProfile(
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody UpdateArtistProfileRequest request,
      HttpServletRequest httpRequest) {
    UpdateArtistProfileResult result =
        updateArtistProfileService.execute(artistProfileMapper.toCommand(user.userId(), request));
    return ApiResponseBody.success(artistProfileMapper.toResponse(result), httpRequest);
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

  private Long requireUserId(AuthUser user) {
    if (user == null) {
      throw new BusinessException(GlobalErrorCode.UNAUTHORIZED);
    }
    return user.userId();
  }
}
