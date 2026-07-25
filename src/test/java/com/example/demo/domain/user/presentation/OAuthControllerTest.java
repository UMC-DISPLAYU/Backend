package com.example.demo.domain.user.presentation;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.application.service.OAuthLoginService;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.presentation.cookie.RefreshTokenCookieManager;
import com.example.demo.domain.user.presentation.cookie.SignupTokenCookieManager;
import com.example.demo.global.error.GlobalExceptionHandler;
import com.example.demo.global.security.JwtProperties;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class OAuthControllerTest {

  private final OAuthLoginService oauthLoginService = mock(OAuthLoginService.class);

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    JwtProperties jwtProperties = new JwtProperties();
    jwtProperties.setRefreshExpiration(1209600000);
    jwtProperties.setSignupExpiration(600000);
    RefreshTokenCookieManager refreshTokenCookieManager =
        new RefreshTokenCookieManager(jwtProperties, false);
    SignupTokenCookieManager signupTokenCookieManager =
        new SignupTokenCookieManager(jwtProperties, false);
    OAuthController controller =
        new OAuthController(
            oauthLoginService,
            refreshTokenCookieManager,
            signupTokenCookieManager,
            "https://www.displayu.co.kr",
            "http://localhost:5173,https://www.displayu.co.kr,https://display-frontend-five.vercel.app",
            false);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void returnsKakaoAuthorizationUrlAndStateCookie() throws Exception {
    when(oauthLoginService.authorizationUrl(eq(Provider.Kakao), anyString()))
        .thenReturn("https://kauth.kakao.com/oauth/authorize?state=state");

    mockMvc
        .perform(get("/api/auth/kakao/login-url"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.success.data.authorizationUrl")
                .value("https://kauth.kakao.com/oauth/authorize?state=state"))
        .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("kakao_oauth_state=")))
        .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("HttpOnly")))
        .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")));
  }

  @Test
  void returnsGoogleAuthorizationUrlAndStateCookie() throws Exception {
    when(oauthLoginService.authorizationUrl(eq(Provider.Google), anyString()))
        .thenReturn("https://accounts.google.com/o/oauth2/v2/auth?state=state");

    mockMvc
        .perform(get("/api/auth/google/login-url"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.success.data.authorizationUrl")
                .value("https://accounts.google.com/o/oauth2/v2/auth?state=state"))
        .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("google_oauth_state=")));
  }

  @Test
  void storesAllowedFrontendOriginForOAuthCallback() throws Exception {
    when(oauthLoginService.authorizationUrl(eq(Provider.Google), anyString()))
        .thenReturn("https://accounts.google.com/o/oauth2/v2/auth?state=state");

    mockMvc
        .perform(
            get("/api/auth/google/login-url")
                .header(HttpHeaders.ORIGIN, "https://display-frontend-five.vercel.app"))
        .andExpect(status().isOk())
        .andExpect(
            header()
                .stringValues(
                    HttpHeaders.SET_COOKIE,
                    hasItem(
                        containsString(
                            "oauth_frontend_origin=https://display-frontend-five.vercel.app"))));
  }

  @Test
  void handlesKakaoAuthorizationCallback() throws Exception {
    LoginResult result = signupResult(Provider.Kakao);
    when(oauthLoginService.loginWithAuthorizationCode(Provider.Kakao, "authorization-code"))
        .thenReturn(result);

    mockMvc
        .perform(
            get("/api/auth/kakao/callback")
                .param("code", "authorization-code")
                .param("state", "state")
                .cookie(new Cookie("kakao_oauth_state", "state")))
        .andExpect(status().isFound())
        .andExpect(header().string(HttpHeaders.LOCATION, "https://www.displayu.co.kr/onboarding"))
        .andExpect(
            header()
                .stringValues(
                    HttpHeaders.SET_COOKIE, hasItem(containsString("signupToken=signup-token"))))
        .andExpect(
            header().stringValues(HttpHeaders.SET_COOKIE, hasItem(containsString("HttpOnly"))));

    verify(oauthLoginService).validateState("state", "state");
    verify(oauthLoginService).loginWithAuthorizationCode(Provider.Kakao, "authorization-code");
  }

  @Test
  void handlesGoogleAuthorizationCallback() throws Exception {
    LoginResult result = signupResult(Provider.Google);
    when(oauthLoginService.loginWithAuthorizationCode(Provider.Google, "authorization-code"))
        .thenReturn(result);

    mockMvc
        .perform(
            get("/api/auth/google/callback")
                .param("code", "authorization-code")
                .param("state", "state")
                .cookie(new Cookie("google_oauth_state", "state")))
        .andExpect(status().isFound())
        .andExpect(header().string(HttpHeaders.LOCATION, "https://www.displayu.co.kr/onboarding"))
        .andExpect(
            header()
                .stringValues(
                    HttpHeaders.SET_COOKIE, hasItem(containsString("signupToken=signup-token"))));

    verify(oauthLoginService).validateState("state", "state");
    verify(oauthLoginService).loginWithAuthorizationCode(Provider.Google, "authorization-code");
  }

  @Test
  void returnsServiceTokensForExistingGoogleUser() throws Exception {
    User user =
        User.builder()
            .id(1L)
            .provider(Provider.Google)
            .providerId("google-user")
            .name("구글 사용자")
            .nickname("google-user")
            .socialEmail("google@example.com")
            .build();
    LoginResult result = LoginResult.login(user, "access-token", "refresh-token");
    when(oauthLoginService.loginWithAuthorizationCode(Provider.Google, "authorization-code"))
        .thenReturn(result);

    mockMvc
        .perform(
            get("/api/auth/google/callback")
                .param("code", "authorization-code")
                .param("state", "state")
                .cookie(new Cookie("google_oauth_state", "state")))
        .andExpect(status().isFound())
        .andExpect(header().string(HttpHeaders.LOCATION, "https://www.displayu.co.kr/home"))
        .andExpect(
            header()
                .stringValues(
                    HttpHeaders.SET_COOKIE, hasItem(containsString("refreshToken=refresh-token"))))
        .andExpect(
            header().stringValues(HttpHeaders.SET_COOKIE, hasItem(containsString("HttpOnly"))))
        .andExpect(
            header().stringValues(HttpHeaders.SET_COOKIE, hasItem(containsString("SameSite=Lax"))));
  }

  @Test
  void returnsServiceTokensForExistingKakaoUser() throws Exception {
    User user =
        User.builder()
            .id(2L)
            .provider(Provider.Kakao)
            .providerId("kakao-user")
            .name("카카오 사용자")
            .nickname("kakao-user")
            .socialEmail("kakao@example.com")
            .build();
    LoginResult result = LoginResult.login(user, "access-token", "refresh-token");
    when(oauthLoginService.loginWithAuthorizationCode(Provider.Kakao, "authorization-code"))
        .thenReturn(result);

    mockMvc
        .perform(
            get("/api/auth/kakao/callback")
                .param("code", "authorization-code")
                .param("state", "state")
                .cookie(new Cookie("kakao_oauth_state", "state")))
        .andExpect(status().isFound())
        .andExpect(header().string(HttpHeaders.LOCATION, "https://www.displayu.co.kr/home"))
        .andExpect(
            header()
                .stringValues(
                    HttpHeaders.SET_COOKIE, hasItem(containsString("refreshToken=refresh-token"))));
  }

  @Test
  void redirectsVercelLoginToVercelOnboarding() throws Exception {
    when(oauthLoginService.loginWithAuthorizationCode(Provider.Google, "authorization-code"))
        .thenReturn(signupResult(Provider.Google));

    mockMvc
        .perform(
            get("/api/auth/google/callback")
                .param("code", "authorization-code")
                .param("state", "state")
                .cookie(
                    new Cookie("google_oauth_state", "state"),
                    new Cookie(
                        "oauth_frontend_origin", "https://display-frontend-five.vercel.app")))
        .andExpect(status().isFound())
        .andExpect(
            header()
                .string(
                    HttpHeaders.LOCATION, "https://display-frontend-five.vercel.app/onboarding"));
  }

  @Test
  void redirectsLocalLoginToLocalHome() throws Exception {
    User user =
        User.builder()
            .id(3L)
            .provider(Provider.Google)
            .providerId("local-google-user")
            .name("Local User")
            .nickname("local-user")
            .socialEmail("local@example.com")
            .build();
    when(oauthLoginService.loginWithAuthorizationCode(Provider.Google, "authorization-code"))
        .thenReturn(LoginResult.login(user, "access-token", "refresh-token"));

    mockMvc
        .perform(
            get("/api/auth/google/callback")
                .param("code", "authorization-code")
                .param("state", "state")
                .cookie(
                    new Cookie("google_oauth_state", "state"),
                    new Cookie("oauth_frontend_origin", "http://localhost:5173")))
        .andExpect(status().isFound())
        .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost:5173/home"));
  }

  @Test
  void redirectsUnknownOriginToDefaultFrontend() throws Exception {
    when(oauthLoginService.loginWithAuthorizationCode(Provider.Kakao, "authorization-code"))
        .thenReturn(signupResult(Provider.Kakao));

    mockMvc
        .perform(
            get("/api/auth/kakao/callback")
                .param("code", "authorization-code")
                .param("state", "state")
                .cookie(
                    new Cookie("kakao_oauth_state", "state"),
                    new Cookie("oauth_frontend_origin", "https://malicious.example")))
        .andExpect(status().isFound())
        .andExpect(header().string(HttpHeaders.LOCATION, "https://www.displayu.co.kr/onboarding"));
  }

  private LoginResult signupResult(Provider provider) {
    return LoginResult.signup(
        "signup-token",
        new SocialUserInfo(provider, "provider-user", "소셜 사용자", "social@example.com"));
  }
}
