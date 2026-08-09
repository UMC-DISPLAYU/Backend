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
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
@RequestMapping("/api/v1/auth")
@Slf4j
public class OAuthController implements OAuthControllerDocs {

  private static final Duration STATE_COOKIE_MAX_AGE = Duration.ofMinutes(5);
  private static final String KAKAO_STATE_COOKIE = "kakao_oauth_state";
  private static final String GOOGLE_STATE_COOKIE = "google_oauth_state";
  private static final String FRONTEND_ORIGIN_COOKIE = "oauth_frontend_origin";

  private final OAuthLoginService oauthLoginService;
  private final RefreshTokenCookieManager refreshTokenCookieManager;
  private final SignupTokenCookieManager signupTokenCookieManager;
  private final String defaultFrontendOrigin;
  private final Set<String> allowedFrontendOrigins;
  private final boolean cookieSecure;

  public OAuthController(
      OAuthLoginService oauthLoginService,
      RefreshTokenCookieManager refreshTokenCookieManager,
      SignupTokenCookieManager signupTokenCookieManager,
      @Value("${frontend.base-url}") String frontendBaseUrl,
      @Value("${frontend.allowed-origins:${frontend.base-url}}") String allowedFrontendOrigins,
      @Value("${app.oauth.cookie-secure:false}") boolean cookieSecure) {
    this.oauthLoginService = oauthLoginService;
    this.refreshTokenCookieManager = refreshTokenCookieManager;
    this.signupTokenCookieManager = signupTokenCookieManager;
    this.defaultFrontendOrigin = normalizeOrigin(frontendBaseUrl);
    this.allowedFrontendOrigins =
        Arrays.stream(allowedFrontendOrigins.split(","))
            .map(String::trim)
            .filter(origin -> !origin.isEmpty())
            .map(this::normalizeOrigin)
            .collect(Collectors.toUnmodifiableSet());
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
      @CookieValue(name = FRONTEND_ORIGIN_COOKIE, required = false) String frontendOrigin,
      HttpServletResponse response) {
    return callback(
        Provider.Kakao, code, state, expectedState, KAKAO_STATE_COOKIE, frontendOrigin, response);
  }

  @Override
  @GetMapping("/google/callback")
  public ResponseEntity<Void> googleCallback(
      @RequestParam String code,
      @RequestParam String state,
      @CookieValue(name = GOOGLE_STATE_COOKIE, required = false) String expectedState,
      @CookieValue(name = FRONTEND_ORIGIN_COOKIE, required = false) String frontendOrigin,
      HttpServletResponse response) {
    return callback(
        Provider.Google, code, state, expectedState, GOOGLE_STATE_COOKIE, frontendOrigin, response);
  }

  private ApiResponseBody<OAuthAuthorizationUrlResponse> authorizationUrl(
      Provider provider,
      String cookieName,
      HttpServletRequest request,
      HttpServletResponse response) {
    String state = UUID.randomUUID().toString();
    addStateCookie(response, cookieName, state, STATE_COOKIE_MAX_AGE);
    addFrontendOriginCookie(
        response, cookieName, resolveFrontendOrigin(request.getHeader(HttpHeaders.ORIGIN)));
    String authorizationUrl = oauthLoginService.authorizationUrl(provider, state);
    return ApiResponseBody.success(new OAuthAuthorizationUrlResponse(authorizationUrl), request);
  }

  private ResponseEntity<Void> callback(
      Provider provider,
      String code,
      String state,
      String expectedState,
      String cookieName,
      String frontendOrigin,
      HttpServletResponse response) {
    log.info(
        "OAuth callback received. provider={}, codePresent={}, statePresent={}, stateCookiePresent={}",
        provider,
        org.springframework.util.StringUtils.hasText(code),
        org.springframework.util.StringUtils.hasText(state),
        org.springframework.util.StringUtils.hasText(expectedState));
    oauthLoginService.validateState(expectedState, state);
    clearStateCookie(response, cookieName);
    clearFrontendOriginCookie(response, cookieName);
    LoginResult result = oauthLoginService.loginWithAuthorizationCode(provider, code);
    String redirectOrigin = resolveFrontendOrigin(frontendOrigin);
    if (result.isNewUser()) {
      signupTokenCookieManager.add(response, result.signupToken());
      return redirect(redirectOrigin, "/onboarding");
    }
    refreshTokenCookieManager.add(response, result.refreshToken());
    return redirect(redirectOrigin, "/home");
  }

  private ResponseEntity<Void> redirect(String frontendOrigin, String path) {
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(frontendOrigin + path))
        .build();
  }

  private void addStateCookie(
      HttpServletResponse response, String name, String value, Duration maxAge) {
    ResponseCookie cookie =
        ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSecure ? "None" : "Lax")
            .path(callbackPath(name))
            .maxAge(maxAge)
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  private void clearStateCookie(HttpServletResponse response, String name) {
    addStateCookie(response, name, "", Duration.ZERO);
  }

  private void addFrontendOriginCookie(
      HttpServletResponse response, String stateCookieName, String frontendOrigin) {
    ResponseCookie cookie =
        ResponseCookie.from(FRONTEND_ORIGIN_COOKIE, frontendOrigin)
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSecure ? "None" : "Lax")
            .path(callbackPath(stateCookieName))
            .maxAge(STATE_COOKIE_MAX_AGE)
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  private void clearFrontendOriginCookie(HttpServletResponse response, String stateCookieName) {
    ResponseCookie cookie =
        ResponseCookie.from(FRONTEND_ORIGIN_COOKIE, "")
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSecure ? "None" : "Lax")
            .path(callbackPath(stateCookieName))
            .maxAge(Duration.ZERO)
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }

  private String resolveFrontendOrigin(String origin) {
    if (origin == null || origin.isBlank()) {
      return defaultFrontendOrigin;
    }
    String normalizedOrigin = normalizeOrigin(origin);
    return allowedFrontendOrigins.contains(normalizedOrigin)
        ? normalizedOrigin
        : defaultFrontendOrigin;
  }

  private String normalizeOrigin(String origin) {
    return origin.replaceFirst("/+$", "");
  }

  private String callbackPath(String cookieName) {
    return KAKAO_STATE_COOKIE.equals(cookieName)
        ? "/api/v1/auth/kakao/callback"
        : "/api/v1/auth/google/callback";
  }
}
