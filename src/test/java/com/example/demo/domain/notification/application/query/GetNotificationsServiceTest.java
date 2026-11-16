package com.example.demo.domain.notification.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.notification.application.result.NotificationCursorResult;
import com.example.demo.domain.notification.application.result.NotificationResult;
import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.repository.NotificationReadStateRepository;
import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import com.example.demo.domain.notification.domain.type.NotificationType;
import com.example.demo.domain.notification.domain.vo.NotificationBodySegment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class GetNotificationsServiceTest {

  private static final Long USER_ID = 7L;
  private static final LocalDateTime LAST_READ_AT = LocalDateTime.of(2026, 11, 16, 9, 0);

  private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
  private final NotificationReadStateRepository notificationReadStateRepository =
      mock(NotificationReadStateRepository.class);
  private final GetNotificationsService service =
      new GetNotificationsService(notificationRepository, notificationReadStateRepository);

  @Test
  void getNotificationsMarksOnlyNotificationsAfterLastReadAtAsUnread() {
    Notification newer = notification(31L, LAST_READ_AT.plusMinutes(1));
    Notification older = notification(30L, LAST_READ_AT.minusMinutes(1));
    when(notificationRepository.findByRecipientBeforeCursorOrderByIdDesc(USER_ID, null, 11))
        .thenReturn(List.of(newer, older));
    when(notificationReadStateRepository.findLastReadAt(USER_ID))
        .thenReturn(Optional.of(LAST_READ_AT));

    NotificationCursorResult result = service.getNotifications(USER_ID, null, 10);

    assertThat(result.notifications())
        .extracting(NotificationResult::notificationId, NotificationResult::isRead)
        .containsExactly(tuple(31L, false), tuple(30L, true));
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursorId()).isNull();
  }

  @Test
  void getNotificationsTreatsAllAsUnreadWhenInboxWasNeverOpened() {
    when(notificationRepository.findByRecipientBeforeCursorOrderByIdDesc(USER_ID, null, 11))
        .thenReturn(List.of(notification(31L, LAST_READ_AT)));
    when(notificationReadStateRepository.findLastReadAt(USER_ID)).thenReturn(Optional.empty());

    NotificationCursorResult result = service.getNotifications(USER_ID, null, 10);

    assertThat(result.notifications())
        .extracting(NotificationResult::isRead)
        .containsExactly(false);
  }

  @Test
  void getNotificationsReturnsNextCursorWhenMoreThanPageSizeExists() {
    when(notificationRepository.findByRecipientBeforeCursorOrderByIdDesc(USER_ID, 40L, 3))
        .thenReturn(
            List.of(
                notification(39L, LAST_READ_AT),
                notification(38L, LAST_READ_AT),
                notification(37L, LAST_READ_AT)));
    when(notificationReadStateRepository.findLastReadAt(USER_ID)).thenReturn(Optional.empty());

    NotificationCursorResult result = service.getNotifications(USER_ID, 40L, 2);

    assertThat(result.notifications())
        .extracting(NotificationResult::notificationId)
        .containsExactly(39L, 38L);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.nextCursorId()).isEqualTo(38L);
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  void getNotificationsClampsPageSizeToMaximum() {
    when(notificationRepository.findByRecipientBeforeCursorOrderByIdDesc(USER_ID, null, 51))
        .thenReturn(List.of());
    when(notificationReadStateRepository.findLastReadAt(USER_ID)).thenReturn(Optional.empty());

    NotificationCursorResult result = service.getNotifications(USER_ID, null, 500);

    verify(notificationRepository).findByRecipientBeforeCursorOrderByIdDesc(USER_ID, null, 51);
    assertThat(result.size()).isEqualTo(50);
    assertThat(result.notifications()).isEmpty();
  }

  private Notification notification(Long id, LocalDateTime createdAt) {
    Notification notification =
        Notification.create(
            USER_ID,
            NotificationType.REQUEST_APPROVED,
            "등록 요청 승인",
            List.of(NotificationBodySegment.plain("승인됐어요.")),
            null);
    ReflectionTestUtils.setField(notification, "id", id);
    ReflectionTestUtils.setField(notification, "createdAt", createdAt);
    return notification;
  }
}
