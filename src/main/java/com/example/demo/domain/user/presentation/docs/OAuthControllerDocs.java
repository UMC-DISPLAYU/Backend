package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.response.OAuthAuthorizationUrlResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "User", description = "사용자 인증 API")
public interface OAuthControllerDocs {

  @Operation(
      summary = "카카오 로그인 URL 발급",
      description =
          """
          카카오 인가 페이지 URL과 CSRF 방지용 state 쿠키를 발급합니다.
          클라이언트는 응답의 authorizationUrl로 브라우저를 이동시켜야 합니다.
          브라우저 요청 시 state 쿠키 전달을 위해 credentials를 포함해야 합니다.
          """)
  @ApiResponse(
      responseCode = "200",
      description = "카카오 로그인 URL 발급 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "카카오 URL 발급 성공", value = KAKAO_AUTH_URL_SUCCESS_EXAMPLE)))
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
  @ApiResponse(
      responseCode = "200",
      description = "구글 로그인 URL 발급 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(name = "구글 URL 발급 성공", value = GOOGLE_AUTH_URL_SUCCESS_EXAMPLE)))
  ApiResponseBody<OAuthAuthorizationUrlResponse> googleAuthorizationUrl(
      HttpServletRequest request, HttpServletResponse response);

  @Operation(
      summary = "카카오 OAuth 콜백",
      description =
          """
          카카오 인가 코드를 검증한 뒤 프론트엔드로 리다이렉트합니다.
          기존 회원은 Refresh Token을 HttpOnly Cookie에 저장하고 토큰 재발급 API를 통해 Access Token을 받도록 홈으로 이동합니다.
          신규 회원은 signupToken을 HttpOnly Cookie에 저장하고 온보딩으로 이동합니다.
          JWT는 리다이렉트 URL에 포함하지 않습니다.
          """)
  @ApiResponse(responseCode = "302", description = "카카오 OAuth 콜백 처리 후 프론트엔드로 리다이렉트")
  ResponseEntity<Void> kakaoCallback(
      String code,
      String state,
      String expectedState,
      String frontendOrigin,
      HttpServletResponse response);

  @Operation(
      summary = "구글 OAuth 콜백",
      description =
          """
          구글 인가 코드를 검증한 뒤 프론트엔드로 리다이렉트합니다.
          기존 회원은 Refresh Token을 HttpOnly Cookie에 저장하고 토큰 재발급 API를 통해 Access Token을 받도록 홈으로 이동합니다.
          신규 회원은 signupToken을 HttpOnly Cookie에 저장하고 온보딩으로 이동합니다.
          JWT는 리다이렉트 URL에 포함하지 않습니다.
          """)
  @ApiResponse(responseCode = "302", description = "구글 OAuth 콜백 처리 후 프론트엔드로 리다이렉트")
  ResponseEntity<Void> googleCallback(
      String code,
      String state,
      String expectedState,
      String frontendOrigin,
      HttpServletResponse response);

  String KAKAO_AUTH_URL_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "authorizationUrl": "https://kauth.kakao.com/oauth/authorize?client_id=...&state=..."
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/oauth/kakao/authorization-url" }
      }
      """;

  String GOOGLE_AUTH_URL_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "authorizationUrl": "https://accounts.google.com/o/oauth2/v2/auth?client_id=...&state=..."
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/oauth/google/authorization-url" }
      }
      """;
}
