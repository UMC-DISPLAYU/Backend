package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.command.AgreementCommand;
import com.example.demo.domain.user.application.command.SignupCommand;
import com.example.demo.domain.user.application.mapper.SignupResponseMapper;
import com.example.demo.domain.user.application.result.SignupResult;
import com.example.demo.domain.user.application.service.AuthService;
import com.example.demo.domain.user.application.service.UserService;
import com.example.demo.domain.user.domain.vo.Nickname;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.domain.user.presentation.cookie.RefreshTokenCookieManager;
import com.example.demo.domain.user.presentation.cookie.SignupTokenCookieManager;
import com.example.demo.domain.user.presentation.docs.AuthControllerDocs;
import com.example.demo.domain.user.presentation.docs.LogoutControllerDocs;
import com.example.demo.domain.user.presentation.docs.RefreshControllerDocs;
import com.example.demo.domain.user.presentation.request.LogoutRequest;
import com.example.demo.domain.user.presentation.request.RefreshRequest;
import com.example.demo.domain.user.presentation.request.SignupRequest;
import com.example.demo.domain.user.presentation.response.RefreshResponse;
import com.example.demo.domain.user.presentation.response.SignupResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import com.example.demo.global.security.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    implements AuthControllerDocs, RefreshControllerDocs, LogoutControllerDocs {

  private final UserService userService;
  private final AuthService authService;

  private final SignupResponseMapper signupResponseMapper;
  private final TokenProvider tokenProvider;
  private final RefreshTokenCookieManager refreshTokenCookieManager;
  private final SignupTokenCookieManager signupTokenCookieManager;

  @Override
  @PostMapping("/signup")
  public ApiResponseBody<SignupResponse.Signup> signup(
      @Valid @RequestBody SignupRequest request,
      @RequestHeader(name = "Authorization", required = false) String authorization,
      @CookieValue(name = "signupToken", required = false) String cookieSignupToken,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {

    String signupToken = resolveSignupToken(authorization, cookieSignupToken);

    SocialUserInfo socialUserInfo = tokenProvider.parseSignupToken(signupToken);

    List<AgreementCommand> agreements =
        request.agreements().stream()
            .map(agreement -> new AgreementCommand(agreement.agreeId(), agreement.isAgreed()))
            .toList();

    SignupCommand command = new SignupCommand(Nickname.of(request.nickname()), agreements);

    SignupResult result = userService.signup(command, socialUserInfo);

    SignupResponse.Signup response =
        signupResponseMapper.toResponse(result.user(), result.accessToken(), result.refreshToken());
    signupTokenCookieManager.clear(httpResponse);

    return ApiResponseBody.success(response, httpRequest);
  }

  @Override
  @PostMapping("/refresh")
  public ApiResponseBody<RefreshResponse> refresh(
      @Valid @RequestBody(required = false) RefreshRequest request,
      @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
      HttpServletRequest httpRequest) {

    String refreshToken =
        resolveRefreshToken(request == null ? null : request.refreshToken(), cookieRefreshToken);
    String accessToken = authService.refresh(refreshToken);

    return ApiResponseBody.success(new RefreshResponse(accessToken), httpRequest);
  }

  @Override
  @PostMapping("/logout")
  public ApiResponseBody<Void> logout(
      @Valid @RequestBody(required = false) LogoutRequest request,
      @CookieValue(name = "refreshToken", required = false) String cookieRefreshToken,
      @AuthenticationPrincipal AuthUser user,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) {

    String refreshToken =
        resolveRefreshToken(request == null ? null : request.refreshToken(), cookieRefreshToken);
    authService.logout(user.userId(), refreshToken);
    refreshTokenCookieManager.clear(httpResponse);

    return ApiResponseBody.success(null, httpRequest);
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

  private String resolveSignupToken(String authorization, String cookieSignupToken) {
    if (StringUtils.hasText(authorization)) {
      return extractSignupToken(authorization);
    }
    if (!StringUtils.hasText(cookieSignupToken)) {
      return extractSignupToken(null);
    }
    return cookieSignupToken;
  }

  private String resolveRefreshToken(String bodyRefreshToken, String cookieRefreshToken) {
    return StringUtils.hasText(bodyRefreshToken) ? bodyRefreshToken : cookieRefreshToken;
  }
}
