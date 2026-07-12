package com.example.demo.domain.user.infrastructure.oauth;

import com.example.demo.domain.user.infrastructure.oauth.dto.GoogleJwkKey;
import com.example.demo.domain.user.infrastructure.oauth.dto.GoogleJwkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class GoogleJwkClient {

  private static final String GOOGLE_JWK_URL = "https://www.googleapis.com/oauth2/v3/certs";

  private final RestTemplate restTemplate;

  public GoogleJwkKey getKey(String kid) {

    GoogleJwkResponse response = restTemplate.getForObject(GOOGLE_JWK_URL, GoogleJwkResponse.class);

    if (response == null || response.getKeys() == null) {

      throw new IllegalStateException("Failed to load Google JWK.");
    }

    return response.getKeys().stream()
        .filter(key -> key.getKid().equals(kid))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No matching Google JWK found."));
  }
}
