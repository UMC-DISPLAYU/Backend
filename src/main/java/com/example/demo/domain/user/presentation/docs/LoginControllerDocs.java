package com.example.demo.domain.user.presentation.docs;

import com.example.demo.domain.user.presentation.request.GoogleLoginRequest;
import com.example.demo.domain.user.presentation.request.KakaoLoginRequest;
import com.example.demo.global.response.ApiResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "User", description = "사용자 인증 API")
public interface LoginControllerDocs {

  @Operation(
      summary = "카카오 소셜 로그인",
      description =
          """
                    카카오에서 발급받은 Access Token으로 사용자 정보를 조회하여 로그인합니다.

                    - 기존 회원: 서비스 Access Token, Refresh Token과 사용자 정보를 반환합니다.
                    - 신규 회원: 추가 회원가입을 위한 signupToken과 소셜 사용자 정보를 반환합니다.
                    - 잘못되거나 만료된 토큰, 다른 Provider의 토큰은 INVALID_SOCIAL_TOKEN 오류를 반환합니다.
                    """)
  ApiResponseBody<?> loginWithKakao(KakaoLoginRequest request, HttpServletRequest httpRequest);

  @Operation(
      summary = "구글 소셜 로그인",
      description =
          """
                    Google Identity에서 발급받은 ID Token을 검증하여 로그인합니다.

                    - 기존 회원: 서비스 Access Token, Refresh Token과 사용자 정보를 반환합니다.
                    - 신규 회원: 추가 회원가입을 위한 signupToken과 소셜 사용자 정보를 반환합니다.
                    - 잘못되거나 만료된 토큰, 다른 Provider의 토큰은 INVALID_SOCIAL_TOKEN 오류를 반환합니다.
                    """)
  ApiResponseBody<?> loginWithGoogle(GoogleLoginRequest request, HttpServletRequest httpRequest);
}
