package com.example.demo.domain.user.infrastructure.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.kakao")
public record KakaoOAuthProperties(Client client, String redirectUri) {

  private static final String CALLBACK_PATH = "/api/auth/kakao/callback";

  public KakaoOAuthProperties {
    if (client == null
        || !StringUtils.hasText(client.id())
        || !StringUtils.hasText(redirectUri)
        || !redirectUri.endsWith(CALLBACK_PATH)) {
      throw new IllegalArgumentException("Kakao OAuth configuration is invalid.");
    }
  }

  public record Client(String id, String secret) {}
}
