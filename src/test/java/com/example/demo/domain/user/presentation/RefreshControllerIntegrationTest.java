package com.example.demo.domain.user.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.domain.user.application.service.AuthService;
import com.example.demo.domain.user.domain.error.AuthErrorCode;
import com.example.demo.global.error.BusinessException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RefreshControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthService authService;

  @Test
  void returnsRefreshTokenNotFoundWhenStoredTokenDoesNotExist() throws Exception {
    assertRefreshError(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND, 401);
  }

  @Test
  void returnsInvalidAccessTokenWhenAccessTokenIsPassedAsRefreshToken() throws Exception {
    assertRefreshError(AuthErrorCode.INVALID_ACCESS_TOKEN, 401);
  }

  @Test
  void returnsWithdrawalUserWhenWithdrawnUserRequestsRefresh() throws Exception {
    assertRefreshError(AuthErrorCode.WITHDRAWAL_USER, 403);
  }

  private void assertRefreshError(AuthErrorCode errorCode, int expectedStatus) throws Exception {
    when(authService.refresh("refresh-token")).thenThrow(new BusinessException(errorCode));

    mockMvc
        .perform(post("/api/v1/auth/refresh").cookie(new Cookie("refreshToken", "refresh-token")))
        .andExpect(status().is(expectedStatus))
        .andExpect(jsonPath("$.error.code").value(errorCode.getCode()));
  }
}
