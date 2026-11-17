package com.example.demo.domain.notification.application.message;

import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.domain.vo.NotificationBodySegment;
import com.example.demo.domain.notification.domain.vo.NotificationTarget;
import java.util.List;
import java.util.Objects;

/** 받는 사람과 상관없이 같은 알림 한 건의 종류·제목·내용·이동 대상. */
public record NotificationMessage(
    NotificationType type,
    String title,
    List<NotificationBodySegment> body,
    NotificationTarget target) {

  public NotificationMessage {
    Objects.requireNonNull(type, "type must not be null.");
    Objects.requireNonNull(title, "title must not be null.");
    body = List.copyOf(Objects.requireNonNull(body, "body must not be null."));
  }
}
