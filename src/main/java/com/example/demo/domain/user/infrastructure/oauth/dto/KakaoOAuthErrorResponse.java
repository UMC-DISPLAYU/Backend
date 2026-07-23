package com.example.demo.domain.user.infrastructure.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoOAuthErrorResponse(
    String error,
    @JsonProperty("error_description") String errorDescription,
    Integer code,
    String msg) {}
