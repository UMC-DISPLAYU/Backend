package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.request.SocialLoginRequest;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "User", description = "사용자 인증 API")
public interface LoginControllerDocs {

  @Operation(
      summary = "소셜 로그인",
      description =
          """
                    카카오/구글 등 소셜 플랫폼에서 발급받은 idToken을 검증하여 로그인합니다.

                    - 기존 회원
                      - Access Token, Refresh Token을 발급합니다.
                      - 사용자 정보를 반환합니다.

                    - 신규 회원
                      - 회원가입 진행을 위한 signupToken을 발급합니다.
                      - 회원가입 API를 통해 추가 정보를 입력합니다.
                    """)
  ApiResponseBody<?> login(SocialLoginRequest request, HttpServletRequest httpRequest);
}
