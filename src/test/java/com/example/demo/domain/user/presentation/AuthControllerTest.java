package com.example.demo.domain.user.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.application.auth.SocialUserInfo;
import com.example.demo.domain.user.application.command.SignupCommand;
import com.example.demo.domain.user.application.mapper.LoginResponseMapper;
import com.example.demo.domain.user.application.mapper.SignupResponseMapper;
import com.example.demo.domain.user.application.result.LoginResult;
import com.example.demo.domain.user.application.result.SignupResult;
import com.example.demo.domain.user.application.service.AuthService;
import com.example.demo.domain.user.application.service.UserService;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.domain.user.presentation.response.LoginResponse;
import com.example.demo.domain.user.presentation.response.SignupResponse;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalExceptionHandler;
import com.example.demo.global.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthControllerTest {

  private final UserService userService = mock(UserService.class);
  private final AuthService authService = mock(AuthService.class);
  private final SignupResponseMapper signupResponseMapper = mock(SignupResponseMapper.class);
  private final LoginResponseMapper loginResponseMapper = mock(LoginResponseMapper.class);
  private final TokenProvider tokenProvider = mock(TokenProvider.class);

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    AuthController controller =
        new AuthController(
            userService, authService, signupResponseMapper, loginResponseMapper, tokenProvider);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void kakaoLoginSelectsKakaoProvider() throws Exception {
    stubSignupResult(Provider.Kakao);

    mockMvc
        .perform(
            post("/api/v1/auth/login/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessToken\":\"kakao-access-token\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.isNewUser").value(true))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/auth/login/kakao"));

    verify(authService).login(Provider.Kakao, "kakao-access-token");
  }

  @Test
  void googleLoginSelectsGoogleProvider() throws Exception {
    stubSignupResult(Provider.Google);

    mockMvc
        .perform(
            post("/api/v1/auth/login/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idToken\":\"google-id-token\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.resultType").value("SUCCESS"))
        .andExpect(jsonPath("$.success.data.isNewUser").value(true))
        .andExpect(jsonPath("$.meta.path").value("/api/v1/auth/login/google"));

    verify(authService).login(Provider.Google, "google-id-token");
  }

  @Test
  void rejectsBlankKakaoAccessToken() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/login/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessToken\":\"   \"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
  }

  @Test
  void rejectsBlankGoogleIdToken() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/login/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idToken\":\"   \"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.resultType").value("FAIL"))
        .andExpect(jsonPath("$.error.code").value("INVALID_INPUT_VALUE"));
  }

  @Test
  void returnsUnauthorizedForInvalidKakaoAccessToken() throws Exception {
    when(authService.login(Provider.Kakao, "invalid-token"))
        .thenThrow(new BusinessException(AuthErrorCode.INVALID_SOCIAL_TOKEN));

    mockMvc
        .perform(
            post("/api/v1/auth/login/kakao")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accessToken\":\"invalid-token\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_SOCIAL_TOKEN"));
  }

  @Test
  void returnsUnauthorizedForInvalidGoogleIdToken() throws Exception {
    when(authService.login(Provider.Google, "invalid-token"))
        .thenThrow(new BusinessException(AuthErrorCode.INVALID_SOCIAL_TOKEN));

    mockMvc
        .perform(
            post("/api/v1/auth/login/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idToken\":\"invalid-token\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_SOCIAL_TOKEN"));
  }

  @Test
  void signsUpWithSocialUserInfoFromSignupToken() throws Exception {
    SocialUserInfo socialUserInfo =
        new SocialUserInfo(Provider.Google, "google-user-id", "구글 사용자", "google@example.com");
    User user =
        User.builder()
            .id(1L)
            .provider(Provider.Google)
            .providerId("google-user-id")
            .name("구글 사용자")
            .nickname("maya")
            .socialEmail("google@example.com")
            .build();
    SignupResult result = new SignupResult(user, "access-token", "refresh-token");
    SignupResponse.Signup response =
        new SignupResponse.Signup(
            new SignupResponse.UserInfo(
                1L, "Google", "구글 사용자", "maya", "google@example.com", null, false),
            "access-token",
            "refresh-token");
    when(tokenProvider.parseSignupToken("signup-token")).thenReturn(socialUserInfo);
    when(userService.signup(ArgumentMatchers.any(SignupCommand.class), eq(socialUserInfo)))
        .thenReturn(result);
    when(signupResponseMapper.toResponse(user, "access-token", "refresh-token"))
        .thenReturn(response);

    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .header("Authorization", "Bearer signup-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "nickname": "maya",
                      "agreements": [
                        {"agreeId": 1, "isAgreed": true},
                        {"agreeId": 2, "isAgreed": true},
                        {"agreeId": 3, "isAgreed": false}
                      ]
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success.data.user.provider").value("Google"))
        .andExpect(jsonPath("$.success.data.accessToken").value("access-token"))
        .andExpect(jsonPath("$.success.data.refreshToken").value("refresh-token"));

    ArgumentCaptor<SignupCommand> commandCaptor = ArgumentCaptor.forClass(SignupCommand.class);
    verify(userService).signup(commandCaptor.capture(), eq(socialUserInfo));
    assertThat(commandCaptor.getValue().nickname().value()).isEqualTo("maya");
    assertThat(commandCaptor.getValue().agreements()).hasSize(3);
  }

  @Test
  void rejectsMalformedSignupAuthorizationHeader() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/auth/signup")
                .header("Authorization", "signup-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "nickname": "maya",
                      "agreements": [{"agreeId": 1, "isAgreed": true}]
                    }
                    """))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_SIGNUP_TOKEN"));
  }

  private void stubSignupResult(Provider provider) {
    SocialUserInfo socialUserInfo =
        new SocialUserInfo(provider, "provider-user", "소셜 사용자", "social@example.com");
    LoginResult result = LoginResult.signup("signup-token", socialUserInfo);
    LoginResponse.Signup response =
        new LoginResponse.Signup(true, "signup-token", provider, "소셜 사용자", "social@example.com");
    String socialToken = provider == Provider.Kakao ? "kakao-access-token" : "google-id-token";
    when(authService.login(provider, socialToken)).thenReturn(result);
    when(loginResponseMapper.toResponse(result)).thenReturn(response);
    when(loginResponseMapper.toSignupResponse(result)).thenReturn(response);
  }
}
