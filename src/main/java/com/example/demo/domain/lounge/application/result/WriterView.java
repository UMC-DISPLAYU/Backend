package com.example.demo.domain.lounge.application.result;

public record WriterView(Long userId, String nickname, String profileImageUrl) {
  public static WriterView unknown(Long userId) {
    return new WriterView(userId, null, null);
  }
}
