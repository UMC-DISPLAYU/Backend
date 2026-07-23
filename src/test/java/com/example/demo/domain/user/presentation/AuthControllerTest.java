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
import com.example.demo.domain.user.application.mapper.SignupResponseMapper;
import com.example.demo.domain.user.application.result.SignupResult;
import com.example.demo.domain.user.application.service.AuthService;
import com.example.demo.domain.user.application.service.UserService;
import com.example.demo.domain.user.domain.aggregate.User;
import com.example.demo.domain.user.domain.enums.Provider;
import com.example.demo.domain.user.presentation.response.SignupResponse;
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
  private final TokenProvider tokenProvider = mock(TokenProvider.class);

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    AuthController controller =
        new AuthController(userService, authService, signupResponseMapper, tokenProvider);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
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
}
