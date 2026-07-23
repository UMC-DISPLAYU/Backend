package com.example.demo.domain.user.infrastructure.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.google")
public record GoogleOAuthProperties(Client client, String redirectUri) {

  private static final String CALLBACK_PATH = "/api/auth/google/callback";

  public GoogleOAuthProperties {
    if (client == null
        || !StringUtils.hasText(client.id())
        || !StringUtils.hasText(client.secret())
        || !StringUtils.hasText(redirectUri)
        || !redirectUri.endsWith(CALLBACK_PATH)) {
      throw new IllegalArgumentException("Google OAuth configuration is invalid.");
    }
  }

  public record Client(String id, String secret) {}
}
