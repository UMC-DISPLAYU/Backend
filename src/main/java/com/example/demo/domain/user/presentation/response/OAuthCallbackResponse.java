package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "OAuth 로그인 결과. 신규 회원 여부에 따라 로그인 또는 회원가입 응답을 반환합니다.",
    oneOf = {LoginResponse.Login.class, LoginResponse.Signup.class})
public sealed interface OAuthCallbackResponse permits LoginResponse.Login, LoginResponse.Signup {}
