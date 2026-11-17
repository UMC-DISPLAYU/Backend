package com.example.demo.domain.notification.domain.repository;

import com.example.demo.domain.notification.domain.aggregate.Notification;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository {

  /** cursorId보다 오래된 알림을 최신순으로 limit개 조회한다. cursorId가 null이면 가장 최신부터 조회한다. */
  List<Notification> findByRecipientBeforeCursorOrderByIdDesc(
      Long recipientUserId, Long cursorId, int limit);

  /** createdAfter 이후에 생긴 알림이 있는지 확인한다. createdAfter가 null이면 알림이 하나라도 있는지 확인한다. */
  boolean existsByRecipientCreatedAfter(Long recipientUserId, LocalDateTime createdAfter);

  List<Notification> saveAll(List<Notification> notifications);
}
