package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.command.AgreementCommand;
import com.example.demo.domain.user.application.command.SignupCommand;
import com.example.demo.domain.user.application.mapper.LoginResponseMapper;
import com.example.demo.domain.user.application.mapper.SignupResponseMapper;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.application.result.SignupResult;
import com.example.demo.domain.user.application.service.AuthService;
import com.example.demo.domain.user.application.service.UserService;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.domain.user.presentation.docs.AuthControllerDocs;
import com.example.demo.domain.user.presentation.docs.LoginControllerDocs;
import com.example.demo.domain.user.presentation.docs.LogoutControllerDocs;
import com.example.demo.domain.user.presentation.docs.RefreshControllerDocs;
import com.example.demo.domain.user.presentation.request.GoogleLoginRequest;
import com.example.demo.domain.user.presentation.request.KakaoLoginRequest;
import com.example.demo.domain.user.presentation.request.LogoutRequest;
import com.example.demo.domain.user.presentation.request.RefreshRequest;
import com.example.demo.domain.user.presentation.request.SignupRequest;
import com.example.demo.domain.user.presentation.response.OAuthCallbackResponse;
import com.example.demo.domain.user.presentation.response.RefreshResponse;
import com.example.demo.domain.user.presentation.response.SignupResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import com.example.demo.global.security.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController
    implements AuthControllerDocs,
        LoginControllerDocs,
        RefreshControllerDocs,
        LogoutControllerDocs {

  private final UserService userService;
  private final AuthService authService;

  private final SignupResponseMapper signupResponseMapper;
  private final LoginResponseMapper loginResponseMapper;

  private final TokenProvider tokenProvider;

  @Override
  @PostMapping("/signup")
  public ApiResponseBody<SignupResponse.Signup> signup(
      @Valid @RequestBody SignupRequest request,
      @RequestHeader("Authorization") String authorization,
      HttpServletRequest httpRequest) {

    String signupToken = extractSignupToken(authorization);

    SocialUserInfo socialUserInfo = tokenProvider.parseSignupToken(signupToken);

    List<AgreementCommand> agreements =
        request.agreements().stream()
            .map(agreement -> new AgreementCommand(agreement.agreeId(), agreement.isAgreed()))
            .toList();

    SignupCommand command = new SignupCommand(Nickname.of(request.nickname()), agreements);

    SignupResult result = userService.signup(command, socialUserInfo);

    SignupResponse.Signup response =
        signupResponseMapper.toResponse(result.user(), result.accessToken(), result.refreshToken());

    return ApiResponseBody.success(response, httpRequest);
  }

  @Override
  @PostMapping("/login/kakao")
  public ApiResponseBody<OAuthCallbackResponse> loginWithKakao(
      @Valid @RequestBody KakaoLoginRequest request, HttpServletRequest httpRequest) {

    return login(Provider.Kakao, request.accessToken(), httpRequest);
  }

  @Override
  @PostMapping("/login/google")
  public ApiResponseBody<OAuthCallbackResponse> loginWithGoogle(
      @Valid @RequestBody GoogleLoginRequest request, HttpServletRequest httpRequest) {

    return login(Provider.Google, request.idToken(), httpRequest);
  }

  @Override
  @PostMapping("/refresh")
  public ApiResponseBody<RefreshResponse> refresh(
      @Valid @RequestBody RefreshRequest request, HttpServletRequest httpRequest) {

    String accessToken = authService.refresh(request.refreshToken());

    return ApiResponseBody.success(new RefreshResponse(accessToken), httpRequest);
  }

  @Override
  @PostMapping("/logout")
  public ApiResponseBody<Void> logout(
      @Valid @RequestBody LogoutRequest request,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest) {

    authService.logout(user.userId(), request.refreshToken());

    return ApiResponseBody.success(null, httpRequest);
  }

  private ApiResponseBody<OAuthCallbackResponse> login(
      Provider provider, String idToken, HttpServletRequest httpRequest) {

    LoginResult result = authService.login(provider, idToken);

    return ApiResponseBody.success(loginResponseMapper.toResponse(result), httpRequest);
  }

  private String extractSignupToken(String authorization) {
    String bearerPrefix = "Bearer ";
    if (!StringUtils.hasText(authorization)
        || !authorization.startsWith(bearerPrefix)
        || !StringUtils.hasText(authorization.substring(bearerPrefix.length()))) {
      throw new BusinessException(AuthErrorCode.INVALID_SIGNUP_TOKEN);
    }
    return authorization.substring(bearerPrefix.length()).trim();
  }
}
