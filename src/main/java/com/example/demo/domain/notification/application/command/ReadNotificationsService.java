package com.example.demo.domain.notification.application.command;

import com.example.demo.domain.notification.domain.repository.NotificationReadStateRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReadNotificationsService {

  private final NotificationReadStateRepository notificationReadStateRepository;
  private final Clock clock;

  public ReadNotificationsService(
      NotificationReadStateRepository notificationReadStateRepository, Clock clock) {
    this.notificationReadStateRepository = notificationReadStateRepository;
    this.clock = clock;
  }

  /** 알림함을 열면 그 시각까지 생긴 알림이 모두 읽음이 된다. 시각은 알림 생성 시각(createdAt)과 같은 UTC 기준으로 남긴다. */
  @Transactional
  public void readAll(Long userId) {
    LocalDateTime now = LocalDateTime.ofInstant(clock.instant(), ZoneOffset.UTC);
    notificationReadStateRepository.markReadAt(userId, now);
  }
}
