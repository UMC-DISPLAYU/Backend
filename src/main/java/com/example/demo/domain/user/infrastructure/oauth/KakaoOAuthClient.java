package com.example.demo.domain.user.infrastructure.oauth;

import com.example.demo.domain.user.infrastructure.oauth.dto.KakaoOAuthErrorResponse;
import com.example.demo.domain.user.infrastructure.oauth.dto.KakaoUserInfoResponse;
import com.example.demo.domain.user.infrastructure.oauth.dto.OAuthTokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

    log.info(
        "Kakao token exchange request. grantType={}, clientIdConfigured={}, redirectUri={}, "
            + "authorizationCodePresent={}, clientSecretConfigured={}",
        form.getFirst("grant_type"),
        StringUtils.hasText(form.getFirst("client_id")),
        form.getFirst("redirect_uri"),
        StringUtils.hasText(form.getFirst("code")),
        StringUtils.hasText(form.getFirst("client_secret")));

    try {
      OAuthTokenResponse response =
          restTemplate.postForObject(TOKEN_URL, formEntity(form), OAuthTokenResponse.class);
      boolean accessTokenPresent = response != null && StringUtils.hasText(response.accessToken());
      log.info("Kakao token exchange succeeded. accessTokenPresent={}", accessTokenPresent);
      if (!accessTokenPresent) {
        throw new IllegalStateException("Kakao token response does not contain an access token.");
      }
      return response.accessToken();
    } catch (RestClientResponseException e) {
      logKakaoApiError("token exchange", e);
      throw new IllegalStateException("Kakao OAuth token exchange failed.", e);
    }
  }

  public KakaoUserInfoResponse getUserInfo(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    log.info(
        "Kakao user info request. endpoint={}, accessTokenPresent={}",
        USER_INFO_URL,
        StringUtils.hasText(accessToken));
    try {
      return restTemplate
          .exchange(
              USER_INFO_URL,
              org.springframework.http.HttpMethod.GET,
              new HttpEntity<>(headers),
              KakaoUserInfoResponse.class)
          .getBody();
    } catch (RestClientResponseException e) {
      logKakaoApiError("user info", e);
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

  private void logKakaoApiError(String stage, RestClientResponseException exception) {
    String responseBody = exception.getResponseBodyAsString();
    KakaoOAuthErrorResponse errorResponse = parseErrorResponse(responseBody);
    log.warn(
        "Kakao OAuth API failed. stage={}, status={}, redirectUri={}, clientIdConfigured={}, "
            + "clientSecretConfigured={}, error={}, errorDescription={}, kakaoCode={}, "
            + "message={}, responseBody={}",
        stage,
        exception.getStatusCode().value(),
        properties.redirectUri(),
        StringUtils.hasText(properties.client().id()),
        StringUtils.hasText(properties.client().secret()),
        errorResponse == null ? null : errorResponse.error(),
        errorResponse == null ? null : errorResponse.errorDescription(),
        errorResponse == null ? null : errorResponse.code(),
        errorResponse == null ? null : errorResponse.msg(),
        safeResponseBody(responseBody));
  }

  private KakaoOAuthErrorResponse parseErrorResponse(String responseBody) {
    if (!StringUtils.hasText(responseBody)) {
      return null;
    }
    try {
      return OBJECT_MAPPER.readValue(responseBody, KakaoOAuthErrorResponse.class);
    } catch (JsonProcessingException ignored) {
      return null;
    }
  }
}
