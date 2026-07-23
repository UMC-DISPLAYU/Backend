package com.example.demo.domain.user.infrastructure.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kakao")
public record KakaoOAuthProperties(Client client, String redirectUri) {

  public record Client(String id, String secret) {}
}
