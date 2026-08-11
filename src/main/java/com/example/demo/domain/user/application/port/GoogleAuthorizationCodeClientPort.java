package com.example.demo.domain.user.application.port;

public interface GoogleAuthorizationCodeClientPort {

  String authorizationUrl(String state);

  String exchangeCode(String code);
}
