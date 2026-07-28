package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.response.RefreshResponse;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "User", description = "사용자 인증 API")
public interface RefreshControllerDocs {

  @Operation(
      summary = "Access Token 재발급",
      description =
          """
                    Refresh Token을 이용하여 새로운 Access Token을 발급합니다.

                    처리 방식:
                    - Refresh Token JWT를 검증합니다.
                    - Refresh Token 만료 여부를 확인합니다.
                    - 서버에 저장된 Refresh Token과 일치하는지 확인합니다.
                    - 탈퇴 회원 여부를 확인합니다.
                    - 새로운 Access Token을 발급합니다.

                    Authorization Header와 Request Body는 필요하지 않습니다.
                    HttpOnly Cookie의 Refresh Token을 사용합니다.
                    """)
  ApiResponseBody<RefreshResponse> refresh(
      @Parameter(hidden = true) String cookieRefreshToken, HttpServletRequest httpRequest);
}
