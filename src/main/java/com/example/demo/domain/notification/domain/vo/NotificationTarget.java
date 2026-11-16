package com.example.demo.domain.notification.domain.vo;

import com.example.demo.domain.notification.domain.type.NotificationTargetType;
import java.util.Objects;

/** 알림을 눌렀을 때 이동할 곳. 화면만 가리키는 경우 id는 null이다. */
public record NotificationTarget(NotificationTargetType type, Long id) {

  public NotificationTarget {
    Objects.requireNonNull(type, "type must not be null.");
  }
}
