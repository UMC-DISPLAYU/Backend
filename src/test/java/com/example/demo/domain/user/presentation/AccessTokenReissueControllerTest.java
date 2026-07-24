package com.example.demo.domain.user.presentation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.application.service.AuthService;
import com.example.demo.domain.user.exception.AuthErrorCode;
import com.example.demo.global.error.BusinessException;
import com.example.demo.global.error.GlobalExceptionHandler;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AccessTokenReissueControllerTest {

  private final AuthService authService = mock(AuthService.class);
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    AccessTokenReissueController controller = new AccessTokenReissueController(authService);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void reissuesAccessTokenFromRefreshTokenCookie() throws Exception {
    when(authService.refresh("refresh-token")).thenReturn("new-access-token");

    mockMvc
        .perform(
            get("/api/auth/reissue")
                .header(HttpHeaders.ORIGIN, "https://display-frontend-five.vercel.app")
                .cookie(new Cookie("refreshToken", "refresh-token")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("new-access-token"));

    verify(authService).refresh("refresh-token");
  }

  @Test
  void returnsExistingAuthErrorForInvalidRefreshToken() throws Exception {
    when(authService.refresh("invalid-refresh-token"))
        .thenThrow(new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));

    mockMvc
        .perform(
            get("/api/auth/reissue").cookie(new Cookie("refreshToken", "invalid-refresh-token")))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_REFRESH_TOKEN"));
  }

  @Test
  void passesMissingRefreshTokenToExistingValidationFlow() throws Exception {
    when(authService.refresh(null))
        .thenThrow(new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));

    mockMvc
        .perform(get("/api/auth/reissue"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error.code").value("INVALID_REFRESH_TOKEN"));

    verify(authService).refresh(null);
  }
}
