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
  @ApiResponse(
      responseCode = "400",
      description = "code 또는 state 누락",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "필수 파라미터 누락",
                      value = KAKAO_CALLBACK_MISSING_VALUE_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "state 또는 카카오 인가 코드 검증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "OAuth 검증 실패",
                      value = KAKAO_CALLBACK_INVALID_TOKEN_EXAMPLE)))
  @ApiResponse(
      responseCode = "403",
      description = "정지된 기존 회원",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "정지 계정", value = KAKAO_ACCOUNT_SUSPENDED_EXAMPLE)))
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
  @ApiResponse(
      responseCode = "400",
      description = "code 또는 state 누락",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "필수 파라미터 누락",
                      value = GOOGLE_CALLBACK_MISSING_VALUE_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "state 또는 구글 인가 코드 검증 실패",
      content =
          @Content(
              mediaType = "application/json",
              examples =
                  @ExampleObject(
                      name = "OAuth 검증 실패",
                      value = GOOGLE_CALLBACK_INVALID_TOKEN_EXAMPLE)))
  @ApiResponse(
      responseCode = "403",
      description = "정지된 기존 회원",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "정지 계정", value = GOOGLE_ACCOUNT_SUSPENDED_EXAMPLE)))
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
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/auth/kakao/login-url" }
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
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/auth/google/login-url" }
      }
      """;

  String KAKAO_CALLBACK_MISSING_VALUE_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "MISSING_REQUIRED_VALUE", "message": "필수 요청 값이 누락되었습니다.", "details": { "field": "code", "message": "필수 요청 파라미터입니다." } }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/auth/kakao/callback" } }
      """;
  String KAKAO_CALLBACK_INVALID_TOKEN_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "INVALID_SOCIAL_TOKEN", "message": "유효하지 않은 소셜 토큰입니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/auth/kakao/callback" } }
      """;
  String KAKAO_ACCOUNT_SUSPENDED_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "ACCOUNT_SUSPENDED", "message": "정지된 계정입니다.", "details": null }, "meta": { "timestamp": "2026-11-15T14:30:00", "path": "/api/v1/auth/kakao/callback" } }
      """;
  String GOOGLE_CALLBACK_MISSING_VALUE_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "MISSING_REQUIRED_VALUE", "message": "필수 요청 값이 누락되었습니다.", "details": { "field": "code", "message": "필수 요청 파라미터입니다." } }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/auth/google/callback" } }
      """;
  String GOOGLE_CALLBACK_INVALID_TOKEN_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "INVALID_SOCIAL_TOKEN", "message": "유효하지 않은 소셜 토큰입니다.", "details": null }, "meta": { "timestamp": "2026-11-14T04:13:15.651", "path": "/api/v1/auth/google/callback" } }
      """;
  String GOOGLE_ACCOUNT_SUSPENDED_EXAMPLE =
      """
      { "resultType": "FAIL", "success": null, "error": { "code": "ACCOUNT_SUSPENDED", "message": "정지된 계정입니다.", "details": null }, "meta": { "timestamp": "2026-11-15T14:30:00", "path": "/api/v1/auth/google/callback" } }
      """;
}
