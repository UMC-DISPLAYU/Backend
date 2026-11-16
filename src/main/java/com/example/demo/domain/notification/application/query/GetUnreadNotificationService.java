package com.example.demo.domain.notification.application.query;

import com.example.demo.domain.notification.application.result.NotificationUnreadResult;
import com.example.demo.domain.notification.domain.repository.NotificationReadStateRepository;
import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetUnreadNotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationReadStateRepository notificationReadStateRepository;

  public GetUnreadNotificationService(
      NotificationRepository notificationRepository,
      NotificationReadStateRepository notificationReadStateRepository) {
    this.notificationRepository = notificationRepository;
    this.notificationReadStateRepository = notificationReadStateRepository;
  }

  @Transactional(readOnly = true)
  public NotificationUnreadResult getUnread(Long userId) {
    LocalDateTime lastReadAt = notificationReadStateRepository.findLastReadAt(userId).orElse(null);
    return new NotificationUnreadResult(
        notificationRepository.existsByRecipientCreatedAfter(userId, lastReadAt));
  }
}
