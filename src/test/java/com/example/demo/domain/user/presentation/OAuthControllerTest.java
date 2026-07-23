package com.example.demo.domain.user.presentation;

import static org.hamcrest.Matchers.containsString;
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
import com.example.demo.domain.user.application.mapper.LoginResponseMapper;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.application.service.OAuthLoginService;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.presentation.response.LoginResponse;
import com.example.demo.global.error.GlobalExceptionHandler;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class OAuthControllerTest {

  private final OAuthLoginService oauthLoginService = mock(OAuthLoginService.class);
  private final LoginResponseMapper loginResponseMapper = mock(LoginResponseMapper.class);

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    OAuthController controller = new OAuthController(oauthLoginService, loginResponseMapper, false);
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
  void handlesKakaoAuthorizationCallback() throws Exception {
    LoginResult result = signupResult(Provider.Kakao);
    LoginResponse.Signup response =
        new LoginResponse.Signup(
            true, "signup-token", Provider.Kakao, "소셜 사용자", "social@example.com");
    when(oauthLoginService.loginWithAuthorizationCode(Provider.Kakao, "authorization-code"))
        .thenReturn(result);
    when(loginResponseMapper.toResponse(result)).thenReturn(response);

    mockMvc
        .perform(
            get("/api/auth/kakao/callback")
                .param("code", "authorization-code")
                .param("state", "state")
                .cookie(new Cookie("kakao_oauth_state", "state")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.isNewUser").value(true))
        .andExpect(jsonPath("$.meta.path").value("/api/auth/kakao/callback"));

    verify(oauthLoginService).validateState("state", "state");
    verify(oauthLoginService).loginWithAuthorizationCode(Provider.Kakao, "authorization-code");
  }

  @Test
  void handlesGoogleAuthorizationCallback() throws Exception {
    LoginResult result = signupResult(Provider.Google);
    LoginResponse.Signup response =
        new LoginResponse.Signup(
            true, "signup-token", Provider.Google, "소셜 사용자", "social@example.com");
    when(oauthLoginService.loginWithAuthorizationCode(Provider.Google, "authorization-code"))
        .thenReturn(result);
    when(loginResponseMapper.toResponse(result)).thenReturn(response);

    mockMvc
        .perform(
            get("/api/auth/google/callback")
                .param("code", "authorization-code")
                .param("state", "state")
                .cookie(new Cookie("google_oauth_state", "state")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.isNewUser").value(true))
        .andExpect(jsonPath("$.meta.path").value("/api/auth/google/callback"));

    verify(oauthLoginService).validateState("state", "state");
    verify(oauthLoginService).loginWithAuthorizationCode(Provider.Google, "authorization-code");
  }

  private LoginResult signupResult(Provider provider) {
    return LoginResult.signup(
        "signup-token",
        new SocialUserInfo(provider, "provider-user", "소셜 사용자", "social@example.com"));
  }
}
