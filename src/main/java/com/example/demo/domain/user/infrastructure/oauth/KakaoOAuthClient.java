package com.example.demo.domain.user.infrastructure.oauth;

import com.example.demo.domain.user.infrastructure.oauth.dto.KakaoUserInfoResponse;
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
public class KakaoOAuthClient {

  private static final String AUTHORIZATION_URL = "https://kauth.kakao.com/oauth/authorize";
  private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
  private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
  private static final String REQUIRED_SCOPES = "profile_nickname,account_email";

  private final RestTemplate restTemplate;
  private final KakaoOAuthProperties properties;

  public KakaoOAuthClient(RestTemplate restTemplate, KakaoOAuthProperties properties) {
    this.restTemplate = restTemplate;
    this.properties = properties;
  }

  public String authorizationUrl(String state) {
    return UriComponentsBuilder.fromUriString(AUTHORIZATION_URL)
        .queryParam("client_id", properties.client().id())
        .queryParam("redirect_uri", properties.redirectUri())
        .queryParam("response_type", "code")
        .queryParam("scope", REQUIRED_SCOPES)
        .queryParam("state", state)
        .build()
        .encode()
        .toUriString();
  }

  public String exchangeCode(String code) {
    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "authorization_code");
    form.add("client_id", properties.client().id());
    form.add("redirect_uri", properties.redirectUri());
    form.add("code", code);
    if (StringUtils.hasText(properties.client().secret())) {
      form.add("client_secret", properties.client().secret());
    }

    try {
      OAuthTokenResponse response =
          restTemplate.postForObject(TOKEN_URL, formEntity(form), OAuthTokenResponse.class);
      if (response == null || !StringUtils.hasText(response.accessToken())) {
        throw new IllegalStateException("Kakao token response does not contain an access token.");
      }
      return response.accessToken();
    } catch (RestClientResponseException e) {
      log.warn(
          "Kakao OAuth token exchange failed. status={}, redirectUri={}, "
              + "clientSecretConfigured={}, responseBody={}",
          e.getStatusCode().value(),
          properties.redirectUri(),
          StringUtils.hasText(properties.client().secret()),
          safeResponseBody(e.getResponseBodyAsString()));
      throw new IllegalStateException("Kakao OAuth token exchange failed.", e);
    }
  }

  public KakaoUserInfoResponse getUserInfo(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    try {
      return restTemplate
          .exchange(
              USER_INFO_URL,
              org.springframework.http.HttpMethod.GET,
              new HttpEntity<>(headers),
              KakaoUserInfoResponse.class)
          .getBody();
    } catch (RestClientResponseException e) {
      log.warn(
          "Kakao user info request failed. status={}, responseBody={}",
          e.getStatusCode().value(),
          safeResponseBody(e.getResponseBodyAsString()));
      throw new IllegalStateException("Kakao user info request failed.", e);
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
