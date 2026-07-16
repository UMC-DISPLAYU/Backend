package com.example.demo.domain.artist.exception;

import com.example.demo.global.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ArtistErrorCode implements BaseErrorCode {
  ARTIST_PROFILE_REQUIRES_VERIFIED_USER(
      HttpStatus.FORBIDDEN,
      "ARTIST_PROFILE_REQUIRES_VERIFIED_USER",
      "학교 이메일 인증을 완료한 사용자만 작가 프로필을 생성할 수 있습니다."),
  ARTIST_PROFILE_ALREADY_EXISTS(
      HttpStatus.CONFLICT, "ARTIST_PROFILE_ALREADY_EXISTS", "이미 작가 프로필이 존재합니다."),
  DUPLICATE_ARTIST_NAME(HttpStatus.CONFLICT, "DUPLICATE_ARTIST_NAME", "이미 사용 중인 작가명입니다."),
  INVALID_ACTIVITY_FIELDS(
      HttpStatus.BAD_REQUEST, "INVALID_ACTIVITY_FIELDS", "활동 분야는 중복 없이 최대 2개까지 선택할 수 있습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
