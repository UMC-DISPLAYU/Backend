package com.example.demo.domain.notification.infrastructure.scheduler;

import com.example.demo.domain.notification.application.command.CleanupNotificationsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 이용자가 적은 새벽 4시(KST)에 오래된 알림을 정리한다. 놓쳐도 다음 날 같은 기준으로 다시 지우므로 보완 실행은 두지 않는다. */
@Component
public class NotificationCleanupScheduler {

  private final CleanupNotificationsService cleanupNotificationsService;

  public NotificationCleanupScheduler(CleanupNotificationsService cleanupNotificationsService) {
    this.cleanupNotificationsService = cleanupNotificationsService;
  }

  @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
  public void cleanup() {
    cleanupNotificationsService.cleanup();
  }
}
