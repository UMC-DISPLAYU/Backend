package com.example.demo.domain.notification.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NotificationReadStateRepository {

  Optional<LocalDateTime> findLastReadAt(Long userId);

  /** 알림함을 연 시각을 기록한다. 이미 더 늦은 시각이 기록돼 있으면 그대로 둔다. */
  void markReadAt(Long userId, LocalDateTime readAt);
}
