package com.example.demo.domain.user.infrastructure.oauth;

import com.example.demo.domain.user.infrastructure.oauth.dto.OAuthTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class GoogleAuthorizationCodeClient {

  private static final String AUTHORIZATION_URL = "https://accounts.google.com/o/oauth2/v2/auth";
  private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

  private final RestTemplate restTemplate;
  private final GoogleOAuthProperties properties;

  public GoogleAuthorizationCodeClient(
      RestTemplate restTemplate, GoogleOAuthProperties properties) {
    this.restTemplate = restTemplate;
    this.properties = properties;
  }

  public String authorizationUrl(String state) {
    log.info(
        "Google OAuth authorization URL created. responseType=code, redirectUri={}, "
            + "statePresent={}",
        properties.redirectUri(),
        StringUtils.hasText(state));
    return UriComponentsBuilder.fromUriString(AUTHORIZATION_URL)
        .queryParam("client_id", properties.client().id())
        .queryParam("redirect_uri", properties.redirectUri())
        .queryParam("response_type", "code")
        .queryParam("scope", "openid profile email")
        .queryParam("state", state)
        .build()
        .encode()
        .toUriString();
  }

  public String exchangeCode(String code) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("client_id", properties.client().id());
    form.add("client_secret", properties.client().secret());
    form.add("redirect_uri", properties.redirectUri());
    form.add("code", code);

    try {
      log.info(
          "Google OAuth token exchange requested. grantType=authorization_code, redirectUri={}, "
              + "authorizationCodePresent={}",
          properties.redirectUri(),
          StringUtils.hasText(code));
      OAuthTokenResponse response =
          restTemplate.postForObject(TOKEN_URL, formEntity(form), OAuthTokenResponse.class);
      if (response == null || !StringUtils.hasText(response.idToken())) {
        log.warn(
            "Google OAuth token response is invalid. responsePresent={}, accessTokenPresent={}, "
                + "idTokenPresent={}",
            response != null,
            response != null && StringUtils.hasText(response.accessToken()),
            response != null && StringUtils.hasText(response.idToken()));
        throw new IllegalStateException("Google token response does not contain an ID token.");
      }
      log.info(
          "Google OAuth token exchange completed. accessTokenPresent={}, idTokenPresent={}",
          StringUtils.hasText(response.accessToken()),
          true);
      return response.idToken();
    } catch (RestClientResponseException e) {
      log.warn(
          "Google OAuth token exchange failed. status={}, redirectUri={}, responseBody={}",
          e.getStatusCode().value(),
          properties.redirectUri(),
          safeResponseBody(e.getResponseBodyAsString()));
      throw new IllegalStateException("Google OAuth token exchange failed.", e);
    }
  }

  private HttpEntity<MultiValueMap<String, String>> formEntity(MultiValueMap<String, String> form) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return new HttpEntity<>(form, headers);
  }

  private String safeResponseBody(String responseBody) {
    if (!StringUtils.hasText(responseBody)) {
      return "<empty>";
    }
    return responseBody.length() <= 1000 ? responseBody : responseBody.substring(0, 1000);
  }
}
