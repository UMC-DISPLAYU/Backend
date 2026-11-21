package com.example.demo.domain.notification.application.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

class CleanupNotificationsServiceTest {

  private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
  private final CleanupNotificationsService service =
      new CleanupNotificationsService(
          notificationRepository,
          Clock.fixed(Instant.parse("2026-11-18T19:00:00Z"), ZoneId.of("Asia/Seoul")));

  @Test
  void cleanupDeletesNotificationsOlderThan180DaysInUtcAndTrimsEachOverflowRecipient() {
    when(notificationRepository.findRecipientsHavingMoreThan(100)).thenReturn(List.of(1L, 2L));

    service.cleanup();

    // 생성 시각은 UTC로 저장되므로 기준도 UTC다(KST 11.19 04:00 = UTC 11.18 19:00).
    verify(notificationRepository).deleteCreatedBefore(LocalDateTime.of(2026, 5, 22, 19, 0));
    verify(notificationRepository).deleteOlderThanLatest(1L, 100);
    verify(notificationRepository).deleteOlderThanLatest(2L, 100);
  }
}
