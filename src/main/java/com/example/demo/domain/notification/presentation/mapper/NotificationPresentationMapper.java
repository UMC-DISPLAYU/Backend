package com.example.demo.domain.notification.presentation.mapper;

import com.example.demo.domain.notification.application.result.NotificationCursorResult;
import com.example.demo.domain.notification.application.result.NotificationResult;
import com.example.demo.domain.notification.application.result.NotificationUnreadResult;
import com.example.demo.domain.notification.domain.vo.NotificationTarget;
import com.example.demo.domain.notification.presentation.response.NotificationBodySegmentResponse;
import com.example.demo.domain.notification.presentation.response.NotificationCursorResponse;
import com.example.demo.domain.notification.presentation.response.NotificationResponse;
import com.example.demo.domain.notification.presentation.response.NotificationTargetResponse;
import com.example.demo.domain.notification.presentation.response.NotificationUnreadResponse;
import org.springframework.stereotype.Component;

@Component
public class NotificationPresentationMapper {

  public NotificationCursorResponse toResponse(NotificationCursorResult result) {
    return new NotificationCursorResponse(
        result.notifications().stream().map(this::toResponse).toList(),
        result.nextCursorId(),
        result.size(),
        result.hasNext());
  }

  public NotificationUnreadResponse toResponse(NotificationUnreadResult result) {
    return new NotificationUnreadResponse(result.hasUnread());
  }

  private NotificationResponse toResponse(NotificationResult result) {
    return new NotificationResponse(
        result.notificationId(),
        result.type(),
        result.title(),
        result.body().stream()
            .map(segment -> new NotificationBodySegmentResponse(segment.text(), segment.bold()))
            .toList(),
        result.isRead(),
        result.createdAt(),
        toResponse(result.target()));
  }

  private NotificationTargetResponse toResponse(NotificationTarget target) {
    return target == null ? null : new NotificationTargetResponse(target.type(), target.id());
  }
}
