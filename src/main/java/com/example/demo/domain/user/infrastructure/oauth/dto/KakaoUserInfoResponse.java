package com.example.demo.domain.user.infrastructure.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserInfoResponse(
    Long id, Properties properties, @JsonProperty("kakao_account") KakaoAccount kakaoAccount) {

  public String nickname() {
    if (kakaoAccount != null && kakaoAccount.profile() != null) {
      return kakaoAccount.profile().nickname();
    }
    return properties == null ? null : properties.nickname();
  }

  public String email() {
    return kakaoAccount == null ? null : kakaoAccount.email();
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Properties(String nickname) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record KakaoAccount(String email, Profile profile) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Profile(String nickname) {}
}
