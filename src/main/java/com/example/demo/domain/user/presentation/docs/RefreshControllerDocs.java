package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.response.RefreshResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "User", description = "사용자 인증 API")
public interface RefreshControllerDocs {

  @Operation(
      summary = "Access Token 재발급",
      description =
          """
                    HttpOnly Cookie에 저장된 Refresh Token을 인증 수단으로 사용하여
                    새로운 Access Token을 발급합니다.

                    Authorization Header를 받지 않는 이유:
                    - 이 API는 Access Token이 없거나 만료된 상황에서 호출합니다.
                    - Access Token 인증을 요구하면 만료된 사용자가 재발급 API를 호출할 수 없습니다.
                    - 따라서 Spring Security의 Access Token 인증 대신 Refresh Token을 직접 검증합니다.

                    처리 방식:
                    - Refresh Token JWT를 검증합니다.
                    - Refresh Token 만료 여부를 확인합니다.
                    - 서버에 저장된 Refresh Token과 일치하는지 확인합니다.
                    - 탈퇴 회원 여부를 확인합니다.
                    - 새로운 Access Token을 발급합니다.

                    Swagger에서 Authorization을 입력하지 않아도 200 OK가 발생할 수 있습니다.
                    이는 Swagger를 실행한 브라우저에 유효한 Refresh Token 쿠키가 저장되어 있으면
                    브라우저가 해당 쿠키를 요청에 자동으로 포함하기 때문입니다.
                    이 경우는 미인증 요청이 아니라 Refresh Token으로 인증된 요청입니다.

                    Refresh Token 쿠키가 없거나, 만료되었거나, 위조되었거나,
                    서버에 저장된 토큰과 일치하지 않으면 401 Unauthorized를 반환합니다.
                    Refresh Token이 만료된 경우에는 OAuth 로그인을 다시 진행해야 합니다.

                    Authorization Header와 Request Body는 사용하지 않습니다.
                    """)
  @ApiResponse(
      responseCode = "200",
      description = "Access Token 재발급 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "재발급 성공", value = REFRESH_SUCCESS_EXAMPLE)))
  @ApiResponse(
      responseCode = "401",
      description = "Refresh Token 누락, 만료, 위조 또는 서버 저장값 불일치",
      content =
          @Content(
              mediaType = "application/json",
              examples = {
                @ExampleObject(
                    name = "유효하지 않은 Refresh Token",
                    value = INVALID_REFRESH_TOKEN_EXAMPLE),
                @ExampleObject(name = "만료된 Refresh Token", value = EXPIRED_REFRESH_TOKEN_EXAMPLE)
              }))
  ApiResponseBody<RefreshResponse> refresh(
      @Parameter(hidden = true) String cookieRefreshToken, HttpServletRequest httpRequest);

  String REFRESH_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": {
            "accessToken": "eyJhbGciOi..."
          }
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/auth/refresh" }
      }
      """;

  String INVALID_REFRESH_TOKEN_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "INVALID_REFRESH_TOKEN",
          "message": "유효하지 않은 리프레시 토큰입니다.",
          "details": null
        },
        "meta": { "timestamp": "2026-08-12T09:00:00", "path": "/api/v1/auth/refresh" }
      }
      """;

  String EXPIRED_REFRESH_TOKEN_EXAMPLE =
      """
      {
        "resultType": "FAIL",
        "success": null,
        "error": {
          "code": "EXPIRED_REFRESH_TOKEN",
          "message": "만료된 리프레시 토큰입니다.",
          "details": null
        },
        "meta": { "timestamp": "2026-08-12T09:00:00", "path": "/api/v1/auth/refresh" }
      }
      """;
}
