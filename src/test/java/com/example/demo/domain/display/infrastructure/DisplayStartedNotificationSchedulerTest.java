package com.example.demo.domain.display.infrastructure;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.display.application.notification.NotifyStartedDisplaysService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class DisplayStartedNotificationSchedulerTest {

  private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

  private final NotifyStartedDisplaysService service = mock(NotifyStartedDisplaysService.class);

  @Test
  void catchUpRunsWhenServerStartsAfterNineInKorea() {
    // 2026-12-04 09:30 KST
    scheduler("2026-12-04T00:30:00Z", true).catchUpAfterStartup();

    verify(service).notifyDisplaysStartingToday();
  }

  @Test
  void catchUpSkipsBeforeNineBecauseScheduledRunWillSendLater() {
    // 2026-12-04 08:59 KST
    scheduler("2026-12-03T23:59:00Z", true).catchUpAfterStartup();

    verify(service, never()).notifyDisplaysStartingToday();
  }

  @Test
  void catchUpSkipsWhenDisabled() {
    scheduler("2026-12-04T00:30:00Z", false).catchUpAfterStartup();

    verify(service, never()).notifyDisplaysStartingToday();
  }

  private DisplayStartedNotificationScheduler scheduler(String instant, boolean catchUp) {
    return new DisplayStartedNotificationScheduler(
        service, Clock.fixed(Instant.parse(instant), SEOUL), catchUp);
  }
}
