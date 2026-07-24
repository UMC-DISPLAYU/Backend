package com.example.demo.domain.user.presentation.cookie;

import com.example.demo.global.security.JwtProperties;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class SignupTokenCookieManager {

  private static final String COOKIE_NAME = "signupToken";
  private static final String COOKIE_PATH = "/";
  private final JwtProperties jwtProperties;
  private final boolean cookieSecure;

  public SignupTokenCookieManager(
      JwtProperties jwtProperties,
      @Value("${app.oauth.cookie-secure:false}") boolean cookieSecure) {
    this.jwtProperties = jwtProperties;
    this.cookieSecure = cookieSecure;
  }

  public void add(HttpServletResponse response, String signupToken) {
    addCookie(response, signupToken, Duration.ofMillis(jwtProperties.getSignupExpiration()));
  }

  public void clear(HttpServletResponse response) {
    addCookie(response, "", Duration.ZERO);
  }

  private void addCookie(HttpServletResponse response, String value, Duration maxAge) {
    ResponseCookie cookie =
        ResponseCookie.from(COOKIE_NAME, value)
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSecure ? "None" : "Lax")
            .path(COOKIE_PATH)
            .maxAge(maxAge)
            .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
  }
}
