package com.example.demo.domain.user.infrastructure.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthTokenResponse(
    @JsonProperty("access_token") String accessToken, @JsonProperty("id_token") String idToken) {}
