package com.example.demo.domain.notification.application.event;

import com.example.demo.domain.notification.application.message.NotificationMessage;
import java.util.List;
import java.util.Objects;

/** 같은 알림을 받는 사람들. 받는 사람 목록은 중복과 직접 한 사람을 이미 걸러 낸 상태다. */
public record NotificationRequestedEvent(List<Long> recipientUserIds, NotificationMessage message) {

  public NotificationRequestedEvent {
    recipientUserIds =
        List.copyOf(Objects.requireNonNull(recipientUserIds, "recipientUserIds must not be null."));
    Objects.requireNonNull(message, "message must not be null.");
  }
}
