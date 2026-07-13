package com.example.demo.domain.lounge.domain.vo;

public record LoungeWriter(Long userId, String nickname, String profileImageUrl) {
  public static LoungeWriter unknown(Long userId) {
    return new LoungeWriter(userId, null, null);
  }
}
