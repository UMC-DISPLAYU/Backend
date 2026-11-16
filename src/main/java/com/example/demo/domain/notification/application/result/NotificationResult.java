package com.example.demo.domain.notification.application.result;

import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.domain.vo.NotificationBodySegment;
import com.example.demo.domain.notification.domain.vo.NotificationTarget;
import java.time.LocalDateTime;
import java.util.List;

public record NotificationResult(
    Long notificationId,
    NotificationType type,
    String title,
    List<NotificationBodySegment> body,
    boolean isRead,
    LocalDateTime createdAt,
    NotificationTarget target) {

  public static NotificationResult of(Notification notification, LocalDateTime lastReadAt) {
    return new NotificationResult(
        notification.getId(),
        notification.getType(),
        notification.getTitle(),
        notification.getBody(),
        notification.isReadAsOf(lastReadAt),
        notification.getCreatedAt(),
        notification.getTarget());
  }
}
