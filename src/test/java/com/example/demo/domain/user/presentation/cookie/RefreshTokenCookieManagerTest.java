package com.example.demo.domain.user.presentation.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.global.security.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

class RefreshTokenCookieManagerTest {

  @Test
  void addsSameSiteNoneSecureCookieWhenCookieSecureIsEnabled() {
    RefreshTokenCookieManager cookieManager = new RefreshTokenCookieManager(jwtProperties(), true);
    MockHttpServletResponse response = new MockHttpServletResponse();

    cookieManager.add(response, "refresh-token");

    assertThat(response.getHeader("Set-Cookie"))
        .contains("refreshToken=refresh-token")
        .contains("HttpOnly")
        .contains("Secure")
        .contains("SameSite=None")
        .contains("Path=/");
  }

  @Test
  void addsSameSiteLaxCookieWhenCookieSecureIsDisabled() {
    RefreshTokenCookieManager cookieManager = new RefreshTokenCookieManager(jwtProperties(), false);
    MockHttpServletResponse response = new MockHttpServletResponse();

    cookieManager.add(response, "refresh-token");

    assertThat(response.getHeader("Set-Cookie"))
        .contains("refreshToken=refresh-token")
        .contains("HttpOnly")
        .doesNotContain("Secure")
        .contains("SameSite=Lax")
        .contains("Path=/");
  }

  private JwtProperties jwtProperties() {
    JwtProperties jwtProperties = new JwtProperties();
    jwtProperties.setRefreshExpiration(7200000);
    return jwtProperties;
  }
}
