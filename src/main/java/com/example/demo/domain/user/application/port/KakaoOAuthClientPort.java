package com.example.demo.domain.user.application.port;

public interface KakaoOAuthClientPort {

  String authorizationUrl(String state);

  String exchangeCode(String code);
}
