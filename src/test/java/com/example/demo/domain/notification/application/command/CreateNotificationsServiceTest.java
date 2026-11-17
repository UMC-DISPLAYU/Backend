package com.example.demo.domain.notification.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.example.demo.domain.notification.application.event.NotificationRequestedEvent;
import com.example.demo.domain.notification.application.message.NotificationMessage;
import com.example.demo.domain.notification.application.message.NotificationMessages;
import com.example.demo.domain.notification.domain.aggregate.Notification;
import com.example.demo.domain.notification.domain.repository.NotificationRepository;
import com.example.demo.domain.notification.domain.type.NotificationType;
import java.util.List;
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
}
