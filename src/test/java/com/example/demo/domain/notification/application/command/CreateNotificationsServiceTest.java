package com.example.demo.domain.notification.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.domain.notification.application.event.NotificationRequestedEvent;
import com.example.demo.domain.notification.application.message.NotificationMessage;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import com.example.demo.domain.notification.domain.type.NotificationType;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CreateNotificationsServiceTest {

  private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
  private final CreateNotificationsService service =
      new CreateNotificationsService(notificationRepository);

  @Test
  @SuppressWarnings("unchecked")
  void createSavesOneNotificationPerRecipientWithSameMessage() {
    NotificationMessage message = NotificationMessages.coAuthorAdded("작품", 7L);

    service.create(new NotificationRequestedEvent(List.of(2L, 3L), message));

    ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
    verify(notificationRepository).saveAll(captor.capture());
    assertThat(captor.getValue())
        .extracting(
            Notification::getRecipientUserId,
            Notification::getType,
            Notification::getTitle,
            Notification::getBody,
            Notification::getTarget)
        .containsExactly(
            tuple(
                2L,
                NotificationType.WORK_COAUTHOR_ADDED,
                message.title(),
                message.body(),
                message.target()),
            tuple(
                3L,
                NotificationType.WORK_COAUTHOR_ADDED,
                message.title(),
                message.body(),
                message.target()));
  }

  @Test
  @SuppressWarnings("unchecked")
  void oncePerTargetTypeSkipsRecipientsAlreadyNotifiedForSameTarget() {
    NotificationMessage message =
        NotificationMessages.exhibitionStarted(
            "전시", LocalDate.of(2026, 12, 4), LocalDate.of(2026, 12, 6), null, 7L);
    when(notificationRepository.findRecipientsAlreadyNotified(
            NotificationType.EXHIBITION_STARTED, 7L, List.of(2L, 3L)))
        .thenReturn(Set.of(2L));

    service.create(new NotificationRequestedEvent(List.of(2L, 3L), message));

    ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
    verify(notificationRepository).saveAll(captor.capture());
    assertThat(captor.getValue()).extracting(Notification::getRecipientUserId).containsExactly(3L);
  }

  @Test
  void oncePerTargetTypeSavesNothingWhenEveryoneAlreadyNotified() {
    NotificationMessage message =
        NotificationMessages.exhibitionStarted(
            "전시", LocalDate.of(2026, 12, 4), LocalDate.of(2026, 12, 6), null, 7L);
    when(notificationRepository.findRecipientsAlreadyNotified(any(), any(), any()))
        .thenReturn(Set.of(2L));

    service.create(new NotificationRequestedEvent(List.of(2L), message));

    verify(notificationRepository, never()).saveAll(any());
  }

  @Test
  void otherTypesAreSavedWithoutCheckingPreviousNotifications() {
    service.create(
        new NotificationRequestedEvent(List.of(2L), NotificationMessages.coAuthorAdded("작품", 7L)));

    verify(notificationRepository, never()).findRecipientsAlreadyNotified(any(), any(), any());
  }
}
