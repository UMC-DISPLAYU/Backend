package com.example.demo.domain.notification.application.command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.notification.domain.repository.NotificationReadStateRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class ReadNotificationsServiceTest {

  private final NotificationReadStateRepository notificationReadStateRepository =
      mock(NotificationReadStateRepository.class);

  @Test
  void readAllRecordsCurrentTimeInUtcEvenWhenClockZoneIsSeoul() {
    Clock seoulClock = Clock.fixed(Instant.parse("2026-11-16T00:30:00Z"), ZoneId.of("Asia/Seoul"));
    ReadNotificationsService service =
        new ReadNotificationsService(notificationReadStateRepository, seoulClock);

    service.readAll(7L);

    verify(notificationReadStateRepository).markReadAt(7L, LocalDateTime.of(2026, 11, 16, 0, 30));
  }
}
