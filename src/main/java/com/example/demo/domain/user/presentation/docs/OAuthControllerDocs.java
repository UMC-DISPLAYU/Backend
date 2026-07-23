package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.response.OAuthAuthorizationUrlResponse;
<<<<<<< HEAD
import com.example.demo.domain.user.presentation.response.OAuthCallbackResponse;
=======
>>>>>>> origin/dev
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "User", description = "사용자 인증 API")
public interface OAuthControllerDocs {

<<<<<<< HEAD
  @Operation(
      summary = "카카오 로그인 URL 발급",
      description =
          """
          카카오 인가 페이지 URL과 CSRF 방지용 state 쿠키를 발급합니다.
          클라이언트는 응답의 authorizationUrl로 브라우저를 이동시켜야 합니다.
          브라우저 요청 시 state 쿠키 전달을 위해 credentials를 포함해야 합니다.
          """)
  ApiResponseBody<OAuthAuthorizationUrlResponse> kakaoAuthorizationUrl(
      HttpServletRequest request, HttpServletResponse response);

  @Operation(
      summary = "구글 로그인 URL 발급",
      description =
          """
          구글 인가 페이지 URL과 CSRF 방지용 state 쿠키를 발급합니다.
          클라이언트는 응답의 authorizationUrl로 브라우저를 이동시켜야 합니다.
          브라우저 요청 시 state 쿠키 전달을 위해 credentials를 포함해야 합니다.
          """)
=======
  @Operation(summary = "카카오 로그인 URL 발급", description = "카카오 인가 페이지 URL과 CSRF 방지용 state 쿠키를 발급합니다.")
  ApiResponseBody<OAuthAuthorizationUrlResponse> kakaoAuthorizationUrl(
      HttpServletRequest request, HttpServletResponse response);

  @Operation(summary = "구글 로그인 URL 발급", description = "구글 인가 페이지 URL과 CSRF 방지용 state 쿠키를 발급합니다.")
>>>>>>> origin/dev
  ApiResponseBody<OAuthAuthorizationUrlResponse> googleAuthorizationUrl(
      HttpServletRequest request, HttpServletResponse response);

  @Operation(
      summary = "카카오 OAuth 콜백",
<<<<<<< HEAD
      description =
          """
          카카오 인가 코드를 Access Token으로 교환합니다.
          기존 회원은 서비스 Access Token과 Refresh Token을 반환하고,
          신규 회원은 추가 회원가입에 사용할 signupToken과 소셜 사용자 정보를 반환합니다.
          """)
  ApiResponseBody<OAuthCallbackResponse> kakaoCallback(
=======
      description = "카카오 인가 코드를 Access Token으로 교환하고 기존 회원 로그인 또는 신규 회원가입 정보를 반환합니다.")
  ApiResponseBody<?> kakaoCallback(
>>>>>>> origin/dev
      String code,
      String state,
      String expectedState,
      HttpServletRequest request,
      HttpServletResponse response);

  @Operation(
      summary = "구글 OAuth 콜백",
<<<<<<< HEAD
      description =
          """
          구글 인가 코드를 ID Token으로 교환합니다.
          기존 회원은 서비스 Access Token과 Refresh Token을 반환하고,
          신규 회원은 추가 회원가입에 사용할 signupToken과 소셜 사용자 정보를 반환합니다.
          """)
  ApiResponseBody<OAuthCallbackResponse> googleCallback(
=======
      description = "구글 인가 코드를 ID Token으로 교환하고 기존 회원 로그인 또는 신규 회원가입 정보를 반환합니다.")
  ApiResponseBody<?> googleCallback(
>>>>>>> origin/dev
      String code,
      String state,
      String expectedState,
      HttpServletRequest request,
      HttpServletResponse response);
}
