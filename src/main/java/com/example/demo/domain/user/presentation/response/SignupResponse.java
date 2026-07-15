package com.example.demo.domain.user.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public class SignupResponse {

  @Builder
  public record Signup(
      @Schema(description = "가입 완료된 사용자 정보") UserInfo user,
      @Schema(description = "Access Token", example = "eyJhbGciOi...") String accessToken,
      @Schema(description = "Refresh Token", example = "eyJhbGciOi...") String refreshToken) {}

  @Builder
  public record UserInfo(
      @Schema(description = "사용자 ID", example = "1") Long id,
      @Schema(description = "소셜 로그인 제공자", example = "KAKAO") String provider,
      @Schema(description = "사용자 이름", example = "마야") String name,
      @Schema(description = "닉네임", example = "maya") String nickname,
      @Schema(description = "소셜 계정 이메일", example = "maya@gmail.com") String socialEmail,
      @Schema(description = "학교 이메일", example = "maya@duksung.ac.kr") String schoolEmail,
      @Schema(description = "학교 이메일 인증 여부", example = "true") boolean isVerified) {}
}
