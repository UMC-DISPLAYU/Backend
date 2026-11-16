package com.example.demo.domain.notification.domain.vo;

import java.util.Objects;

/** 알림 내용의 한 조각. 작품명·전시명처럼 사용자가 입력한 값이 문장에 섞이므로 강조 구간을 기호로 표시하지 않고 조각으로 나눠 저장한다. */
public record NotificationBodySegment(String text, boolean bold) {

  public NotificationBodySegment {
    Objects.requireNonNull(text, "text must not be null.");
  }

  public static NotificationBodySegment plain(String text) {
    return new NotificationBodySegment(text, false);
  }

  public static NotificationBodySegment bold(String text) {
    return new NotificationBodySegment(text, true);
  }
}
