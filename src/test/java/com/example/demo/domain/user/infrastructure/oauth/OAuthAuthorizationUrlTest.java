package com.example.demo.domain.user.infrastructure.oauth;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
<<<<<<< HEAD
import static org.assertj.core.api.Assertions.assertThatThrownBy;
=======
>>>>>>> origin/dev
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

<<<<<<< HEAD
import com.example.demo.domain.user.infrastructure.oauth.dto.KakaoUserInfoResponse;
import com.example.demo.domain.user.infrastructure.oauth.dto.OAuthTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

@ExtendWith(OutputCaptureExtension.class)
=======
import com.example.demo.domain.user.infrastructure.oauth.dto.OAuthTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

>>>>>>> origin/dev
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
<<<<<<< HEAD
        .contains("scope=profile_nickname,account_email")
=======
>>>>>>> origin/dev
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
<<<<<<< HEAD

  @Test
  @SuppressWarnings("unchecked")
  void kakaoTokenRequestOmitsClientSecretWhenItIsNotConfigured() {
    KakaoOAuthProperties properties =
        new KakaoOAuthProperties(
            new KakaoOAuthProperties.Client("kakao-client-id", null),
            "http://localhost:8080/api/auth/kakao/callback");
    KakaoOAuthClient client = new KakaoOAuthClient(restTemplate, properties);
    ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> requestCaptor =
        ArgumentCaptor.forClass(HttpEntity.class);
    when(restTemplate.postForObject(
            eq("https://kauth.kakao.com/oauth/token"),
            requestCaptor.capture(),
            eq(OAuthTokenResponse.class)))
        .thenReturn(new OAuthTokenResponse("kakao-access-token", null));

    client.exchangeCode("authorization-code");

    assertThat(requestCaptor.getValue().getBody()).doesNotContainKey("client_secret");
  }

  @Test
  void kakaoUserInfoRequestUsesBearerAccessToken() {
    KakaoOAuthProperties properties =
        new KakaoOAuthProperties(
            new KakaoOAuthProperties.Client("kakao-client-id", null),
            "http://localhost:8080/api/auth/kakao/callback");
    KakaoOAuthClient client = new KakaoOAuthClient(restTemplate, properties);
    ArgumentCaptor<HttpEntity<Void>> requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    KakaoUserInfoResponse response =
        new KakaoUserInfoResponse(
            1234L,
            null,
            new KakaoUserInfoResponse.KakaoAccount(
                "kakao@example.com", new KakaoUserInfoResponse.Profile("카카오 사용자"), false, false));
    when(restTemplate.exchange(
            eq("https://kapi.kakao.com/v2/user/me"),
            eq(HttpMethod.GET),
            requestCaptor.capture(),
            eq(KakaoUserInfoResponse.class)))
        .thenReturn(ResponseEntity.ok(response));

    KakaoUserInfoResponse result = client.getUserInfo("kakao-access-token");

    assertThat(result).isEqualTo(response);
    assertThat(requestCaptor.getValue().getHeaders().getFirst("Authorization"))
        .isEqualTo("Bearer kakao-access-token");
  }

  @Test
  void logsOriginalKakaoTokenErrorResponse(CapturedOutput output) {
    KakaoOAuthProperties properties =
        new KakaoOAuthProperties(
            new KakaoOAuthProperties.Client("kakao-client-id", null),
            "http://localhost:8080/api/auth/kakao/callback");
    KakaoOAuthClient client = new KakaoOAuthClient(restTemplate, properties);
    String responseBody =
        """
        {
          "error": "invalid_grant",
          "error_description": "authorization code not found"
        }
        """;
    when(restTemplate.postForObject(
            eq("https://kauth.kakao.com/oauth/token"),
            org.mockito.ArgumentMatchers.any(HttpEntity.class),
            eq(OAuthTokenResponse.class)))
        .thenThrow(
            HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                HttpHeaders.EMPTY,
                responseBody.getBytes(UTF_8),
                UTF_8));

    assertThatThrownBy(() -> client.exchangeCode("authorization-code"))
        .isInstanceOf(IllegalStateException.class);

    assertThat(output)
        .contains("stage=token exchange")
        .contains("status=400")
        .contains("error=invalid_grant")
        .contains("errorDescription=authorization code not found")
        .contains("clientSecretConfigured=false");
  }
=======
>>>>>>> origin/dev
}
