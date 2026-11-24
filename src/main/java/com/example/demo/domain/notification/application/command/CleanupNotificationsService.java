package com.example.demo.domain.notification.application.command;

import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 알림함 보관 정책: 180일이 지나거나 사람별 100개를 넘으면 오래된 것부터 지운다. 알림을 직접 지우는 기능은 없다. */
@Service
public class CleanupNotificationsService {

  static final int RETENTION_DAYS = 180;
  static final int MAX_NOTIFICATIONS_PER_USER = 100;

  private static final Logger log = LoggerFactory.getLogger(CleanupNotificationsService.class);

  private final NotificationRepository notificationRepository;
  private final Clock clock;

  public CleanupNotificationsService(NotificationRepository notificationRepository, Clock clock) {
    this.notificationRepository = notificationRepository;
    this.clock = clock;
  }

  @Transactional
  public void cleanup() {
    // 알림 생성 시각은 UTC로 저장된다.
    LocalDateTime createdBefore =
        LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC).minusDays(RETENTION_DAYS);
    int expired = notificationRepository.deleteCreatedBefore(createdBefore);

    List<Long> overflowRecipients =
        notificationRepository.findRecipientsHavingMoreThan(MAX_NOTIFICATIONS_PER_USER);
    int overflow = 0;
    for (Long recipientUserId : overflowRecipients) {
      overflow +=
          notificationRepository.deleteOlderThanLatest(recipientUserId, MAX_NOTIFICATIONS_PER_USER);
    }
    log.info(
        "Notification cleanup finished. expired={}, overflow={}, overflowRecipients={}",
        expired,
        overflow,
        overflowRecipients.size());
  }
}
