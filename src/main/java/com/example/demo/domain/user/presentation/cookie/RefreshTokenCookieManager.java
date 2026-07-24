package com.example.demo.domain.user.presentation.cookie;

import com.example.demo.global.security.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCookieManager {

  private static final String COOKIE_NAME = "refreshToken";
  private static final String COOKIE_PATH = "/";
  private static final String SAME_SITE = "Lax";

  private final JwtProperties jwtProperties;
  private final boolean cookieSecure;

  public RefreshTokenCookieManager(
      JwtProperties jwtProperties,
      @Value("${app.oauth.cookie-secure:false}") boolean cookieSecure) {
    this.jwtProperties = jwtProperties;
    this.cookieSecure = cookieSecure;
  }

  public void add(HttpServletResponse response, String refreshToken) {
    addCookie(response, refreshToken, Duration.ofMillis(jwtProperties.getRefreshExpiration()));
  }

  public void clear(HttpServletResponse response) {
    addCookie(response, "", Duration.ZERO);
  }

  private void addCookie(HttpServletResponse response, String value, Duration maxAge) {
    ResponseCookie cookie =
        ResponseCookie.from(COOKIE_NAME, value)
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(SAME_SITE)
            .path(COOKIE_PATH)
            .maxAge(maxAge)
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
