package com.example.demo.domain.user.presentation.response;

import com.example.demo.domain.user.domain.enums.Provider;
import io.swagger.v3.oas.annotations.media.Schema;

public class LoginResponse {

  public record Login(
      @Schema(description = "신규 회원 여부", example = "false") boolean isNewUser,
      @Schema(description = "Access Token", example = "eyJhbGciOi...") String accessToken,
      @Schema(description = "Refresh Token", example = "eyJhbGciOi...") String refreshToken,
      @Schema(description = "사용자 정보") UserInfo user)
      implements OAuthCallbackResponse {}

  public record Signup(
      @Schema(description = "신규 회원 여부", example = "true") boolean isNewUser,
      @Schema(description = "회원가입용 Signup Token", example = "eyJhbGciOi...") String signupToken,
      @Schema(description = "소셜 로그인 제공자", example = "Kakao") Provider provider,
      @Schema(description = "소셜 계정 이름", example = "마야") String name,
      @Schema(description = "소셜 계정 이메일", example = "maya@gmail.com") String socialEmail)
      implements OAuthCallbackResponse {}

  public record UserInfo(
      @Schema(description = "사용자 ID", example = "1") Long id,
      @Schema(description = "소셜 로그인 제공자", example = "Kakao") Provider provider,
      @Schema(description = "소셜 플랫폼 사용자 ID", example = "123456789") String providerId,
      @Schema(description = "사용자 이름", example = "마야") String name,
      @Schema(description = "닉네임", example = "maya") String nickname,
      @Schema(description = "소셜 계정 이메일", example = "maya@gmail.com") String socialEmail,
      @Schema(description = "학교 이메일", example = "maya@duksung.ac.kr") String schoolEmail,
      @Schema(description = "학교 이메일 인증 여부", example = "false") boolean isVerified) {}
}
