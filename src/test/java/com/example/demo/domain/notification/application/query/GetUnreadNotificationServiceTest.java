package com.example.demo.domain.notification.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.domain.notification.domain.repository.NotificationReadStateRepository;
import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class GetUnreadNotificationServiceTest {

  private static final Long USER_ID = 7L;

  private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
  private final NotificationReadStateRepository notificationReadStateRepository =
      mock(NotificationReadStateRepository.class);
  private final GetUnreadNotificationService service =
      new GetUnreadNotificationService(notificationRepository, notificationReadStateRepository);

  @Test
  void getUnreadChecksNotificationsCreatedAfterLastReadAt() {
    LocalDateTime lastReadAt = LocalDateTime.of(2026, 11, 16, 9, 0);
    when(notificationReadStateRepository.findLastReadAt(USER_ID))
        .thenReturn(Optional.of(lastReadAt));
    when(notificationRepository.existsByRecipientCreatedAfter(USER_ID, lastReadAt))
        .thenReturn(true);

    assertThat(service.getUnread(USER_ID).hasUnread()).isTrue();
  }

  @Test
  void getUnreadChecksAnyNotificationWhenInboxWasNeverOpened() {
    when(notificationReadStateRepository.findLastReadAt(USER_ID)).thenReturn(Optional.empty());
    when(notificationRepository.existsByRecipientCreatedAfter(USER_ID, null)).thenReturn(false);

    assertThat(service.getUnread(USER_ID).hasUnread()).isFalse();
  }
}
