package com.example.demo.domain.user.infrastructure.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.google")
public record GoogleOAuthProperties(Client client, String redirectUri) {

  public record Client(String id, String secret) {}
}
