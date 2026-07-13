package com.example.demo.domain.user.infrastructure.oauth;

import com.example.demo.domain.user.infrastructure.oauth.dto.KakaoJwkKey;
import com.example.demo.domain.user.infrastructure.oauth.dto.KakaoJwkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoJwkClient {

  private static final String KAKAO_JWK_URL = "https://kauth.kakao.com/.well-known/jwks.json";

  private final RestTemplate restTemplate;

  public KakaoJwkKey getKey(String kid) {

    KakaoJwkResponse response = restTemplate.getForObject(KAKAO_JWK_URL, KakaoJwkResponse.class);

    if (response == null || response.getKeys() == null) {
      throw new IllegalStateException("Failed to load Kakao JWK.");
    }

    return response.getKeys().stream()
        .filter(key -> key.getKid().equals(kid))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No matching JWK found."));
  }
}
