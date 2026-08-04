package com.example.demo.domain.user.presentation.docs;

import com.example.demo.global.response.ApiResponseBody;
import com.example.demo.global.security.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "User", description = "사용자 인증 API")
public interface LogoutControllerDocs {

  @Operation(
      summary = "로그아웃",
      description =
          """
                    현재 로그인된 사용자를 로그아웃 처리합니다.

                    - Access Token으로 사용자를 인증합니다.
                    - HttpOnly Cookie의 Refresh Token을 검증한 뒤 저장된 Refresh Token을 삭제합니다.
                    - Request Body에 Refresh Token을 입력하지 않습니다.
                    - 로그아웃 완료 후 기존 Refresh Token은 사용할 수 없습니다.
                    """)
  @SecurityRequirement(name = "Authorization")
  @ApiResponse(
      responseCode = "200",
      description = "로그아웃 성공",
      content =
          @Content(
              mediaType = "application/json",
              examples = @ExampleObject(name = "로그아웃 성공", value = LOGOUT_SUCCESS_EXAMPLE)))
  ApiResponseBody<Void> logout(
      @Parameter(hidden = true) String cookieRefreshToken,
      @Parameter(hidden = true) AuthUser user,
      HttpServletRequest httpRequest,
      HttpServletResponse httpResponse);

  String LOGOUT_SUCCESS_EXAMPLE =
      """
      {
        "resultType": "SUCCESS",
        "success": {
          "data": null
        },
        "error": null,
        "meta": { "timestamp": "2026-08-04T09:00:00", "path": "/api/v1/auth/logout" }
      }
      """;
}
