package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.response.OAuthAuthorizationUrlResponse;
import com.example.demo.domain.user.presentation.response.OAuthCallbackResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "User", description = "사용자 인증 API")
public interface OAuthControllerDocs {

  @Operation(summary = "카카오 로그인 URL 발급", description = "카카오 인가 페이지 URL과 CSRF 방지용 state 쿠키를 발급합니다.")
  ApiResponseBody<OAuthAuthorizationUrlResponse> kakaoAuthorizationUrl(
      HttpServletRequest request, HttpServletResponse response);

  @Operation(summary = "구글 로그인 URL 발급", description = "구글 인가 페이지 URL과 CSRF 방지용 state 쿠키를 발급합니다.")
  ApiResponseBody<OAuthAuthorizationUrlResponse> googleAuthorizationUrl(
      HttpServletRequest request, HttpServletResponse response);

  @Operation(
      summary = "카카오 OAuth 콜백",
      description = "카카오 인가 코드를 Access Token으로 교환하고 기존 회원 로그인 또는 신규 회원가입 정보를 반환합니다.")
  ApiResponseBody<OAuthCallbackResponse> kakaoCallback(
      String code,
      String state,
      String expectedState,
      HttpServletRequest request,
      HttpServletResponse response);

  @Operation(
      summary = "구글 OAuth 콜백",
      description = "구글 인가 코드를 ID Token으로 교환하고 기존 회원 로그인 또는 신규 회원가입 정보를 반환합니다.")
  ApiResponseBody<OAuthCallbackResponse> googleCallback(
      String code,
      String state,
      String expectedState,
      HttpServletRequest request,
      HttpServletResponse response);
}
