package com.example.demo.domain.display.infrastructure;

import com.example.demo.domain.display.application.notification.NotifyStartedDisplaysService;
import java.time.Clock;
import java.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 매일 오전 9시(KST)에 저장한 전시 시작 알림을 보낸다. 9시에 서버가 꺼져 있었을 수 있으므로(배포 재시작 등) 9시 이후에 서버가 켜지면 한 번 더 돈다. 이미 받은
 * 사람은 알림 모듈이 빼므로 다시 돌아도 놓친 사람만 받는다.
 */
@Component
public class DisplayStartedNotificationScheduler {

  static final LocalTime SEND_TIME = LocalTime.of(9, 0);

  private final NotifyStartedDisplaysService notifyStartedDisplaysService;
  private final Clock clock;
  private final boolean catchUpOnStartup;

  public DisplayStartedNotificationScheduler(
      NotifyStartedDisplaysService notifyStartedDisplaysService,
      Clock clock,
      @Value("${notification.exhibition-started.catch-up-on-startup:true}")
          boolean catchUpOnStartup) {
    this.notifyStartedDisplaysService = notifyStartedDisplaysService;
    this.clock = clock;
    this.catchUpOnStartup = catchUpOnStartup;
  }

  @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
  public void notifyDisplaysStartingToday() {
    notifyStartedDisplaysService.notifyDisplaysStartingToday();
  }

  @EventListener(ApplicationReadyEvent.class)
  public void catchUpAfterStartup() {
    if (catchUpOnStartup && !LocalTime.now(clock).isBefore(SEND_TIME)) {
      notifyStartedDisplaysService.notifyDisplaysStartingToday();
    }
  }
}
