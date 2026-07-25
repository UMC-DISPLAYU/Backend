package com.example.demo.domain.user.presentation;

import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.application.service.OAuthLoginService;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.presentation.cookie.RefreshTokenCookieManager;
import com.example.demo.domain.user.presentation.cookie.SignupTokenCookieManager;
import com.example.demo.domain.user.presentation.docs.OAuthControllerDocs;
import com.example.demo.domain.user.presentation.response.OAuthAuthorizationUrlResponse;
import com.example.demo.global.response.ApiResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class OAuthController implements OAuthControllerDocs {

  private static final Duration STATE_COOKIE_MAX_AGE = Duration.ofMinutes(5);
  private static final String KAKAO_STATE_COOKIE = "kakao_oauth_state";
  private static final String GOOGLE_STATE_COOKIE = "google_oauth_state";

  private final OAuthLoginService oauthLoginService;
  private final RefreshTokenCookieManager refreshTokenCookieManager;
  private final SignupTokenCookieManager signupTokenCookieManager;
  private final URI homeRedirectUri;
  private final URI onboardingRedirectUri;
  private final boolean cookieSecure;

  public OAuthController(
      OAuthLoginService oauthLoginService,
      RefreshTokenCookieManager refreshTokenCookieManager,
      SignupTokenCookieManager signupTokenCookieManager,
      @Value("${frontend.base-url}") String frontendBaseUrl,
      @Value("${app.oauth.cookie-secure:false}") boolean cookieSecure) {
    this.oauthLoginService = oauthLoginService;
    this.refreshTokenCookieManager = refreshTokenCookieManager;
    this.signupTokenCookieManager = signupTokenCookieManager;
    String normalizedFrontendBaseUrl = frontendBaseUrl.replaceFirst("/+$", "");
    this.homeRedirectUri = URI.create(normalizedFrontendBaseUrl + "/home");
    this.onboardingRedirectUri = URI.create(normalizedFrontendBaseUrl + "/onboarding");
    this.cookieSecure = cookieSecure;
  }

  @Override
  @GetMapping("/kakao/login-url")
  public ApiResponseBody<OAuthAuthorizationUrlResponse> kakaoAuthorizationUrl(
      HttpServletRequest request, HttpServletResponse response) {
    return authorizationUrl(Provider.Kakao, KAKAO_STATE_COOKIE, request, response);
  }

  @Override
  @GetMapping("/google/login-url")
  public ApiResponseBody<OAuthAuthorizationUrlResponse> googleAuthorizationUrl(
      HttpServletRequest request, HttpServletResponse response) {
    return authorizationUrl(Provider.Google, GOOGLE_STATE_COOKIE, request, response);
  }

  @Override
  @GetMapping("/kakao/callback")
  public ResponseEntity<Void> kakaoCallback(
      @RequestParam String code,
      @RequestParam String state,
      @CookieValue(name = KAKAO_STATE_COOKIE, required = false) String expectedState,
      HttpServletResponse response) {
    return callback(Provider.Kakao, code, state, expectedState, KAKAO_STATE_COOKIE, response);
  }

  @Override
  @GetMapping("/google/callback")
  public ResponseEntity<Void> googleCallback(
      @RequestParam String code,
      @RequestParam String state,
      @CookieValue(name = GOOGLE_STATE_COOKIE, required = false) String expectedState,
      HttpServletResponse response) {
    return callback(Provider.Google, code, state, expectedState, GOOGLE_STATE_COOKIE, response);
  }

  private ApiResponseBody<OAuthAuthorizationUrlResponse> authorizationUrl(
      Provider provider,
      String cookieName,
      HttpServletRequest request,
      HttpServletResponse response) {
    String state = UUID.randomUUID().toString();
    addStateCookie(response, cookieName, state, STATE_COOKIE_MAX_AGE);
    String authorizationUrl = oauthLoginService.authorizationUrl(provider, state);
    return ApiResponseBody.success(new OAuthAuthorizationUrlResponse(authorizationUrl), request);
  }

  private ResponseEntity<Void> callback(
      Provider provider,
      String code,
      String state,
      String expectedState,
      String cookieName,
      HttpServletResponse response) {
    log.info(
        "OAuth callback received. provider={}, codePresent={}, statePresent={}, stateCookiePresent={}",
        provider,
        org.springframework.util.StringUtils.hasText(code),
        org.springframework.util.StringUtils.hasText(state),
        org.springframework.util.StringUtils.hasText(expectedState));
    oauthLoginService.validateState(expectedState, state);
    clearStateCookie(response, cookieName);
    LoginResult result = oauthLoginService.loginWithAuthorizationCode(provider, code);
    if (result.isNewUser()) {
      signupTokenCookieManager.add(response, result.signupToken());
      return redirect(onboardingRedirectUri);
    }
    refreshTokenCookieManager.add(response, result.refreshToken());
    return redirect(homeRedirectUri);
  }

  private ResponseEntity<Void> redirect(URI redirectUri) {
    return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
  }

  private void addStateCookie(
      HttpServletResponse response, String name, String value, Duration maxAge) {
    ResponseCookie cookie =
        ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite("Lax")
            .path(callbackPath(name))
            .maxAge(maxAge)
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  private void clearStateCookie(HttpServletResponse response, String name) {
    addStateCookie(response, name, "", Duration.ZERO);
  }

  private String callbackPath(String cookieName) {
    return KAKAO_STATE_COOKIE.equals(cookieName)
        ? "/api/auth/kakao/callback"
        : "/api/auth/google/callback";
  }
}
