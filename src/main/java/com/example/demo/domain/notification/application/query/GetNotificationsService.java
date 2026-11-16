package com.example.demo.domain.notification.application.query;

import com.example.demo.domain.notification.application.result.NotificationCursorResult;
import com.example.demo.domain.notification.application.result.NotificationResult;
import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.repository.NotificationReadStateRepository;
import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetNotificationsService {

  private static final int MAX_PAGE_SIZE = 50;

  private final NotificationRepository notificationRepository;
  private final NotificationReadStateRepository notificationReadStateRepository;

  public GetNotificationsService(
      NotificationRepository notificationRepository,
      NotificationReadStateRepository notificationReadStateRepository) {
    this.notificationRepository = notificationRepository;
    this.notificationReadStateRepository = notificationReadStateRepository;
  }

  /** 읽음 여부는 알림함을 열기 전 기준으로 계산한다. 읽음 처리는 FE가 목록을 받은 뒤 따로 요청한다. */
  @Transactional(readOnly = true)
  public NotificationCursorResult getNotifications(Long userId, Long cursorId, int size) {
    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    List<Notification> fetched =
        notificationRepository.findByRecipientBeforeCursorOrderByIdDesc(
            userId, cursorId, pageSize + 1);

    boolean hasNext = fetched.size() > pageSize;
    List<Notification> notifications = hasNext ? fetched.subList(0, pageSize) : fetched;

    LocalDateTime lastReadAt = notificationReadStateRepository.findLastReadAt(userId).orElse(null);
    List<NotificationResult> results =
        notifications.stream()
            .map(notification -> NotificationResult.of(notification, lastReadAt))
            .toList();

    Long nextCursorId = hasNext ? results.get(results.size() - 1).notificationId() : null;
    return new NotificationCursorResult(results, nextCursorId, pageSize, hasNext);
  }
}
