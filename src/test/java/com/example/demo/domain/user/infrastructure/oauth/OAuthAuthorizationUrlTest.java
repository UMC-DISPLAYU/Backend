package com.example.demo.domain.user.infrastructure.oauth;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.user.infrastructure.oauth.dto.OAuthTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

class OAuthAuthorizationUrlTest {

  private static final String STATE = "random-state";

  private RestTemplate restTemplate;

  @BeforeEach
  void setUp() {
    restTemplate = mock(RestTemplate.class);
  }

  @Test
  void googleAuthorizationUrlUsesGoogleRedirectUri() {
    GoogleOAuthProperties properties =
        new GoogleOAuthProperties(
            new GoogleOAuthProperties.Client("google-client-id", "google-client-secret"),
            "http://localhost:8080/api/auth/google/callback");
    GoogleAuthorizationCodeClient client =
        new GoogleAuthorizationCodeClient(restTemplate, properties);

    String authorizationUrl = UriUtils.decode(client.authorizationUrl(STATE), UTF_8);

    assertThat(authorizationUrl)
        .startsWith("https://accounts.google.com/o/oauth2/v2/auth?")
        .contains("client_id=google-client-id")
        .contains("redirect_uri=http://localhost:8080/api/auth/google/callback")
        .contains("response_type=code")
        .contains("scope=openid profile email")
        .contains("state=" + STATE)
        .doesNotContain("/api/auth/kakao/callback");
  }

  @Test
  void kakaoAuthorizationUrlUsesKakaoRedirectUri() {
    KakaoOAuthProperties properties =
        new KakaoOAuthProperties(
            new KakaoOAuthProperties.Client("kakao-client-id", "kakao-client-secret"),
            "http://localhost:8080/api/auth/kakao/callback");
    KakaoOAuthClient client = new KakaoOAuthClient(restTemplate, properties);

    String authorizationUrl = UriUtils.decode(client.authorizationUrl(STATE), UTF_8);

    assertThat(authorizationUrl)
        .startsWith("https://kauth.kakao.com/oauth/authorize?")
        .contains("client_id=kakao-client-id")
        .contains("redirect_uri=http://localhost:8080/api/auth/kakao/callback")
        .contains("response_type=code")
        .contains("state=" + STATE)
        .doesNotContain("/api/auth/google/callback");
  }

  @Test
  @SuppressWarnings("unchecked")
  void googleTokenRequestUsesGoogleClientAndRedirectUri() {
    GoogleOAuthProperties properties =
        new GoogleOAuthProperties(
            new GoogleOAuthProperties.Client("google-client-id", "google-client-secret"),
            "http://localhost:8080/api/auth/google/callback");
    GoogleAuthorizationCodeClient client =
        new GoogleAuthorizationCodeClient(restTemplate, properties);
    ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> requestCaptor =
        ArgumentCaptor.forClass(HttpEntity.class);
    when(restTemplate.postForObject(
            eq("https://oauth2.googleapis.com/token"),
            requestCaptor.capture(),
            eq(OAuthTokenResponse.class)))
        .thenReturn(new OAuthTokenResponse("google-access-token", "google-id-token"));

    String idToken = client.exchangeCode("authorization-code");

    MultiValueMap<String, String> form = requestCaptor.getValue().getBody();
    assertThat(idToken).isEqualTo("google-id-token");
    assertThat(form).isNotNull();
    assertThat(form.getFirst("grant_type")).isEqualTo("authorization_code");
    assertThat(form.getFirst("client_id")).isEqualTo("google-client-id");
    assertThat(form.getFirst("client_secret")).isEqualTo("google-client-secret");
    assertThat(form.getFirst("redirect_uri"))
        .isEqualTo("http://localhost:8080/api/auth/google/callback");
    assertThat(form.getFirst("code")).isEqualTo("authorization-code");
  }

  @Test
  void rejectsKakaoCallbackConfiguredAsGoogleRedirectUri() {
    assertThatIllegalArgumentException()
        .isThrownBy(
            () ->
                new GoogleOAuthProperties(
                    new GoogleOAuthProperties.Client("google-client-id", "google-client-secret"),
                    "http://localhost:8080/api/auth/kakao/callback"))
        .withMessage("Google OAuth configuration is invalid.");
  }

  @Test
  @SuppressWarnings("unchecked")
  void kakaoTokenRequestUsesKakaoClientAndRedirectUri() {
    KakaoOAuthProperties properties =
        new KakaoOAuthProperties(
            new KakaoOAuthProperties.Client("kakao-client-id", "kakao-client-secret"),
            "http://localhost:8080/api/auth/kakao/callback");
    KakaoOAuthClient client = new KakaoOAuthClient(restTemplate, properties);
    ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> requestCaptor =
        ArgumentCaptor.forClass(HttpEntity.class);
    when(restTemplate.postForObject(
            eq("https://kauth.kakao.com/oauth/token"),
            requestCaptor.capture(),
            eq(OAuthTokenResponse.class)))
        .thenReturn(new OAuthTokenResponse("kakao-access-token", null));

    String accessToken = client.exchangeCode("authorization-code");

    MultiValueMap<String, String> form = requestCaptor.getValue().getBody();
    assertThat(accessToken).isEqualTo("kakao-access-token");
    assertThat(form).isNotNull();
    assertThat(form.getFirst("grant_type")).isEqualTo("authorization_code");
    assertThat(form.getFirst("client_id")).isEqualTo("kakao-client-id");
    assertThat(form.getFirst("client_secret")).isEqualTo("kakao-client-secret");
    assertThat(form.getFirst("redirect_uri"))
        .isEqualTo("http://localhost:8080/api/auth/kakao/callback");
    assertThat(form.getFirst("code")).isEqualTo("authorization-code");
  }

  @Test
  void rejectsGoogleCallbackConfiguredAsKakaoRedirectUri() {
    assertThatIllegalArgumentException()
        .isThrownBy(
            () ->
                new KakaoOAuthProperties(
                    new KakaoOAuthProperties.Client("kakao-client-id", "kakao-client-secret"),
                    "http://localhost:8080/api/auth/google/callback"))
        .withMessage("Kakao OAuth configuration is invalid.");
  }
}
